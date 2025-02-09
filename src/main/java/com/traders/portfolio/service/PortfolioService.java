package com.traders.portfolio.service;

import com.traders.common.model.MarketDetailsRequest;
import com.traders.common.model.MarketQuotes;
import com.traders.common.utils.CommonValidations;
import com.traders.portfolio.domain.OrderCategory;
import com.traders.portfolio.domain.Portfolio;
import com.traders.portfolio.domain.PortfolioStock;
import com.traders.portfolio.domain.Stock;
import com.traders.portfolio.domain.Transaction;
import com.traders.portfolio.exception.BadRequestAlertException;
import com.traders.portfolio.repository.PortfolioRepository;
import com.traders.portfolio.repository.PortfolioStocksDetailRepository;
import com.traders.portfolio.repository.TransactionRepository;
import com.traders.portfolio.service.dto.PortfolioDTO;
import com.traders.portfolio.service.dto.PortfolioStockDTO;
import com.traders.portfolio.service.dto.TradeRequest;
import com.traders.portfolio.utils.PortfolioServiceContext;
import com.traders.portfolio.validations.AbstractConfigValidator;
import com.traders.portfolio.validations.future.EquityFutureConfValidator;
import com.traders.portfolio.validations.future.MCXFutureConfValidator;
import com.traders.portfolio.validations.options.EquityIndexOptionsConfValidator;
import com.traders.portfolio.validations.options.EquityOptionsConfValidator;
import com.traders.portfolio.validations.options.MCXOptionsConfValidator;
import com.traders.portfolio.web.rest.fign.DhanFeignService;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.tuple.Pair;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

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

    public PortfolioService(PortfolioRepository portfolioRepository, RedisService redisService, ModelMapper modelMapper, DhanFeignService service, StockService stockService, TransactionRepository transactionRepository, PortfolioStocksDetailRepository portfolioStocksDetailRepository, UserConfigurationService userConfigurationService, WalletService walletService) {
        this.portfolioRepository = portfolioRepository;
        this.redisService = redisService;
        this.modelMapper = modelMapper;
        this.exchangeClient = service;
        this.stockService = stockService;
        this.transactionRepository = transactionRepository;
        this.portfolioStocksDetailRepository = portfolioStocksDetailRepository;
        this.walletService = walletService;
    }

    @Transactional
    public PortfolioDTO getUserPortfolio(String userId) {
        long id;
        if ((id = CommonValidations.getNumber(userId, Long.class)) == 0)
            throw new BadRequestAlertException("Invalid User details", "PortfolioService", "Not valid user passed in request");

        return getPortfolio(id)
                .map(this::getPortfolioDTO).orElseGet(PortfolioDTO::new);

    }

    private PortfolioDTO getPortfolioDTO(Portfolio portfolio) {
        PortfolioDTO portfolioDTO = new PortfolioDTO();
        modelMapper.map(portfolio, portfolioDTO);
        portfolioDTO.setStocks(mapQuotesToDTO(portfolioDTO.getStocks()));
        var portfolioPrice = portfolioDTO.getStocks()
                .stream()
                .reduce(
                        Pair.of(0.0, 0.0), // Initial accumulator: (investment, currentValue, unused)
                        (acc, stock) -> Pair.of(
                                acc.getLeft() + stock.getQuantity() * stock.getStock().getLastPrice(), // Update investment
                                acc.getRight() + stock.getQuantity() * stock.getAverageCost()
                        ),
                        (acc1, acc2) -> Pair.of(
                                acc1.getLeft() + acc2.getLeft(),
                                acc1.getRight() + acc2.getRight()
                        )
                );

        portfolioDTO.calculatePortfolio(portfolioPrice);
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
        AbstractConfigValidator configValidator = getConfValidator(stockInstance.getExchange(), stockInstance.getInstrumentType());
        Double margin=configValidator.getMargin(userId, tradeRequest.orderValidity());
        Double balance= walletService.getCurrentBalance(userId);
        double marginUsed=((tradeRequest.lotSize()*tradeRequest.askedPrice())/margin);
        if(balance<marginUsed){
            //TODO: Throw error here
        }

        Portfolio portfolio = getPortfolio(userId).orElseGet(() -> new Portfolio(userId));
        Portfolio savedPortfolio = savePortfolio(portfolio);

        tradeRequest.orderType().setQuantity(tradeRequest.lotSize()*stockInstance.getLotSize());

        PortfolioStock portfolioStockDetails = getPortfolioStock(tradeRequest, portfolio, savedPortfolio, stockInstance);
        PortfolioStock savedPortfolioStockDetails = portfolioStocksDetailRepository.save(portfolioStockDetails);

        var transactions = tradeRequest.orderCategory().addTransaction(marginUsed,savedPortfolioStockDetails, tradeRequest, transactionRepository);
        subscribeInstrument(savedPortfolioStockDetails, tradeRequest);
        closeStockDeal(savedPortfolioStockDetails);
        return transactions.stream().map(Transaction::getId).toList();
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

        if(tradeRequest.orderCategory() == OrderCategory.MARKET || tradeRequest.orderCategory() == OrderCategory.BRACKET_AT_MARKET){
            portfolioStockDetails.addQuantity(tradeRequest.orderType().getQuantity(),tradeRequest.askedPrice());
        }else{
            portfolioStockDetails.setQuantity(tradeRequest.lotSize());
        }
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

    private AbstractConfigValidator getConfValidator(String exchange, String instrumentType) {
        AbstractConfigValidator validator = null;
        if ("MCX".equalsIgnoreCase(exchange)) {
            if ("FUTCOM".equalsIgnoreCase(instrumentType)) {
                validator = PortfolioServiceContext.getBean(MCXFutureConfValidator.class);
            } else if ("OPTFUT".equalsIgnoreCase(instrumentType)) {
                validator = PortfolioServiceContext.getBean(MCXOptionsConfValidator.class);
            }
        } else if ("NSE".equalsIgnoreCase(exchange)) {
            if ("FUTSTK".equalsIgnoreCase(instrumentType)) {
                validator = PortfolioServiceContext.getBean(EquityFutureConfValidator.class);
            } else if ("OPTSTK".equalsIgnoreCase(instrumentType)) {
                validator = PortfolioServiceContext.getBean(EquityOptionsConfValidator.class);
            } else if ("OPTIDX".equalsIgnoreCase(instrumentType)) {
                validator = PortfolioServiceContext.getBean(EquityIndexOptionsConfValidator.class);
            }
        }
        return validator;
    }
}