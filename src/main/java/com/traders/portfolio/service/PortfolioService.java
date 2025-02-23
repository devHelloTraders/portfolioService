package com.traders.portfolio.service;

import com.traders.common.model.MarketDetailsRequest;
import com.traders.common.model.MarketQuotes;
import com.traders.common.utils.CommonValidations;
import com.traders.portfolio.domain.*;
import com.traders.portfolio.exception.BadRequestAlertException;
import com.traders.portfolio.repository.PortfolioRepository;
import com.traders.portfolio.repository.PortfolioStocksDetailRepository;
import com.traders.portfolio.repository.TransactionRepository;
import com.traders.portfolio.service.dto.PortfolioDTO;
import com.traders.portfolio.service.dto.PortfolioStockDTO;
import com.traders.portfolio.service.dto.TradeRequest;
import com.traders.portfolio.trades.dto.ActiveTradesResponseDTO;
import com.traders.portfolio.trades.service.TradesService;
import com.traders.portfolio.web.rest.fign.DhanFeignService;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.tuple.Pair;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final RedisService redisService;
    private final ModelMapper modelMapper;
    private final DhanFeignService exchangeClient;
    private final StockService stockService;
    private final TransactionRepository transactionRepository;
    private final PortfolioStocksDetailRepository portfolioStocksDetailRepository;
    private final WalletService walletService;
    private final TradesService tradesService;

    public PortfolioService(PortfolioRepository portfolioRepository, RedisService redisService, ModelMapper modelMapper, DhanFeignService service, StockService stockService, TransactionRepository transactionRepository, PortfolioStocksDetailRepository portfolioStocksDetailRepository, UserConfigurationService userConfigurationService, WalletService walletService, TradesService tradesService) {
        this.portfolioRepository = portfolioRepository;
        this.redisService = redisService;
        this.modelMapper = modelMapper;
        this.exchangeClient = service;
        this.stockService = stockService;
        this.transactionRepository = transactionRepository;
        this.portfolioStocksDetailRepository = portfolioStocksDetailRepository;
        this.walletService = walletService;
        this.tradesService = tradesService;
    }

    @Transactional
    public PortfolioDTO getUserPortfolio(String userId) {
        long id;
        if ((id = CommonValidations.getNumber(userId, Long.class)) == 0)
            throw new BadRequestAlertException("Invalid User details", "PortfolioService", "Not valid user passed in request");

        Double profitLoss=0.0;
        Double marginAvailable=0.0;
        Double moneyToMarket=0.0;
        Double marginUsed=0.0;
        List<ActiveTradesResponseDTO> activeTrades=tradesService.getAllActiveTrades(Collections.singletonMap("userId",id));
        for(ActiveTradesResponseDTO trade:activeTrades){
            profitLoss+=trade.getProfitLoss();
            marginUsed+=trade.getMargin();
        }

        PortfolioDTO portfolioDTO = new PortfolioDTO();
        portfolioDTO.setProfitLoss(profitLoss);
        portfolioDTO.setLedgerBalance(walletService.getCurrentBalance(id));
        portfolioDTO.setM2m(moneyToMarket);
        portfolioDTO.setAvailableMargin(marginAvailable);
        portfolioDTO.setActiveTrades(activeTrades);

        return portfolioDTO;
    }


    private Optional<Portfolio> getPortfolio(long userId) {
        return portfolioRepository.findByUserId(userId);
    }

    private Portfolio savePortfolio(Portfolio portfolio) {
        return portfolioRepository.save(portfolio);
    }

    public Set<PortfolioStockDTO> mapQuotesToDTO(Set<PortfolioStockDTO> portfolioStocksDTOList) {
        MarketDetailsRequest request = new MarketDetailsRequest();
        var filteredList = portfolioStocksDTOList.stream().map(PortfolioStockDTO::getStock)
                .map(stock -> {
                    stock.setQuotes((MarketQuotes) redisService.getStockValue(String.valueOf(stock.getInstrumentToken())));
                    stock.updatePrice();
                    request.addInstrument(MarketDetailsRequest.InstrumentDetails.of(stock.getInstrumentToken(), stock.getExchangeSegment(), stock.getTradingSymbol()));
                    return stock;
                }).filter(stock -> stock.getQuotes() == null || stock.getLastPrice() == 0.0)
                .toList();
        var marketResponse = exchangeClient.getQuotesFromMarketList(request);
        filteredList.forEach(stockDTO -> {
            MarketQuotes quotes = marketResponse.get(String.valueOf(stockDTO.getInstrumentToken()));
            stockDTO.setQuotes(quotes);
            stockDTO.updatePrice();
        });
        return portfolioStocksDTOList;
    }

    public List<Long> addTransactionToPortfolio(Long userId, TradeRequest tradeRequest) {
        Stock stockInstance = stockService.getStock(tradeRequest.stockId());
        Double balance= walletService.getCurrentBalance(userId);

        Portfolio portfolio = getPortfolio(userId).orElseGet(() -> new Portfolio(userId));
        Portfolio savedPortfolio = savePortfolio(portfolio);

        tradeRequest.orderType().setQuantity(tradeRequest.lotSize()*stockInstance.getLotSize());

        PortfolioStock portfolioStockDetails = getPortfolioStock(tradeRequest, portfolio, savedPortfolio, stockInstance);
        boolean shortSell=(portfolioStockDetails.getQuantity()<0);
        if(tradeRequest.orderCategory() == OrderCategory.MARKET || tradeRequest.orderCategory() == OrderCategory.BRACKET_AT_MARKET){
            portfolioStockDetails.addQuantity(tradeRequest.orderType().getQuantity(),tradeRequest.askedPrice());
        }else{
            portfolioStockDetails.setQuantity(tradeRequest.orderType().getQuantity());
        }
        PortfolioStock savedPortfolioStockDetails = portfolioStocksDetailRepository.save(portfolioStockDetails);

        var transactions = tradeRequest.orderCategory().addTransaction(balance,portfolioStockDetails, tradeRequest, transactionRepository,shortSell);
        subscribeInstrument(savedPortfolioStockDetails, tradeRequest);
        closeStockDeal(savedPortfolioStockDetails);
        updateWalletBalance(userId,transactions,tradeRequest);
        return transactions.stream().map(Transaction::getId).collect(Collectors.toList());
    }

    private static PortfolioStock getPortfolioStock(TradeRequest tradeRequest, Portfolio portfolio, Portfolio savedPortfolio, Stock stockInstance) {
        PortfolioStock portfolioStockDetails = portfolio.getStocks().stream()
                .filter(Objects::nonNull)
                .filter(portfolioStock->portfolioStock.getStock()!=null)
                .filter(portfolioStock -> Objects.equals(portfolioStock.getOrderValidity(),tradeRequest.orderValidity()))
                .filter(portfolioStock-> Objects.equals(portfolioStock.getStock().getId(), tradeRequest.stockId()))
                .findFirst()
                .orElseGet(()->{
                    var profileStock = new PortfolioStock(savedPortfolio, stockInstance, tradeRequest.orderValidity());
                    savedPortfolio.getStocks().add(profileStock);
                    return profileStock;
                });
        return portfolioStockDetails;
    }

    /**
     * This private method is intended to close all the
     * transaction for a given {@link PortfolioStock}.
     * This will set deleteflag to 1 for all the related {@link Transaction}s
     * and it-self as well.
     *
     * @param portfolioStock Stock/Instrument for which deal should be closed.
     */
    private void closeStockDeal(PortfolioStock portfolioStock) {
        if (portfolioStock.getQuantity() == 0 && portfolioStock.getIsCompleted()) {
            transactionRepository.closeTransaction(portfolioStock);
            portfolioStocksDetailRepository.closeInstrumentDeal(portfolioStock.getId());
        }
    }

    private void updateWalletBalance(Long userId,List<Transaction> transactions,TradeRequest tradeRequest) {
        transactions.forEach(transaction -> {
            Double profitLoss = transaction.getProfitLoss();
            double margin = transaction.getMargin();
            String stockName = transaction.getPortfolioStock().getStock().getName();
            String orderType = tradeRequest.orderType().name();
            double lotSize = tradeRequest.lotSize();

            if (profitLoss != null) {
                String profitLossType = profitLoss > 0 ? "PROFIT" : "LOSS";
                String remarks = String.format("%s booked after %s %f lotsize of %s", profitLossType, orderType, lotSize, stockName);
                WalletTransactionType transactionType = profitLoss > 0 ? WalletTransactionType.PROFIT : WalletTransactionType.LOSS;
                walletService.updateCurrentBalance(userId, profitLoss>0?profitLoss:-profitLoss, transactionType, remarks);
            }

            if (margin > 0) {
                String remarks = String.format("%f Margin used for %s, %f lots of %s", margin, orderType, lotSize, stockName);
                walletService.updateCurrentBalance(userId, -margin, WalletTransactionType.MARGIN, remarks);
            }
        });

    }

    private void subscribeInstrument(PortfolioStock portfolioStockDetails, TradeRequest tradeRequest) {
        MarketDetailsRequest request = MarketDetailsRequest.get();
        if (portfolioStockDetails.getStock() != null && Objects.equals(portfolioStockDetails.getQuantity(), tradeRequest.orderType().getQuantity())) {
            request.addInstrument(MarketDetailsRequest.InstrumentDetails.of(portfolioStockDetails.getStock().getInstrumentToken(),
                    portfolioStockDetails.getStock().getExchangeSegment(), portfolioStockDetails.getStock().getName()));
        } else if (portfolioStockDetails.getStock() != null
                && portfolioStockDetails.getQuantity() == 0
                && (tradeRequest.orderCategory() == OrderCategory.BRACKET_AT_MARKET || tradeRequest.orderCategory() == OrderCategory.MARKET)) {
            request.removeInstrument(MarketDetailsRequest.InstrumentDetails.of(portfolioStockDetails.getStock().getInstrumentToken(),
                    portfolioStockDetails.getStock().getExchangeSegment(), portfolioStockDetails.getStock().getName()));
        }
        exchangeClient.subScribeInstruments(request);
    }
}