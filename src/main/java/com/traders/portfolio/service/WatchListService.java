package com.traders.portfolio.service;

import com.traders.common.model.MarketDetailsRequest;
import com.traders.common.model.MarketQuotes;
import com.traders.common.utils.CommonValidations;
import com.traders.portfolio.domain.Stock;
import com.traders.portfolio.domain.WatchList;
import com.traders.portfolio.domain.WatchlistStock;
import com.traders.portfolio.exception.BadRequestAlertException;
import com.traders.portfolio.repository.WatchListRepository;
import com.traders.portfolio.service.dto.WatchListDTO;
import com.traders.portfolio.service.dto.WatchListStockDTO;
import com.traders.portfolio.utils.CustomSorter;
import com.traders.portfolio.web.rest.fign.DhanFeignService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class WatchListService {

    private final WatchListRepository watchListRepository;
    private final StockService stockService;
    private final ModelMapper modelMapper;
    private final WatchListStockService watchListStockService;
    private final DhanFeignService exchangeClient;
    private final RedisService redisService;
    public WatchListService(WatchListRepository watchListRepository, StockService stockService, ModelMapper modelMapper, WatchListStockService watchListStockService, DhanFeignService exchangeClient, RedisService redisService) {
        this.watchListRepository = watchListRepository;
        this.stockService = stockService;
        this.modelMapper = modelMapper;
        this.watchListStockService = watchListStockService;
        this.exchangeClient = exchangeClient;
        this.redisService = redisService;
    }
    @Transactional
    public WatchListDTO getWatchList(String userId){
        long id;
        if((id =CommonValidations.getNumber(userId,Long.class))==0)
            throw new BadRequestAlertException("Invalid User details", "Watchlist service", "Not valid user passed in request");

        return getWatchList(id)
                .map(this::getWatchListDTO).orElseGet(WatchListDTO::new);
    }

    @Transactional
    public void removeFromWatchList(String userId, List<Long> stocksToDelete){
        long id;
        if((id =CommonValidations.getNumber(userId,Long.class))==0)
            throw new BadRequestAlertException("Invalid User details", "Watchlist service", "Not valid user passed in request");

        var watchList = getWatchList(id)
                        .orElseThrow(()->
                                new BadRequestAlertException("Invalid User details", "Watchlist service", "No watchlist is associated with user"));
        var watchListStock = watchListStockService.getAllWatchlistStock(watchList.getId());
        var deletedStocks = watchListStock
                                     .stream()
                                     .filter(stock->stocksToDelete.contains(stock.getId()))
                                     .map(stock -> {
                                                    stock.setWatchList(null);
                                                    return stock;
                                        })
                                     .toList();
        if(stocksToDelete.size()!=deletedStocks.size())
            throw new BadRequestAlertException("Invalid Stock details", "Watchlist service", "Not valid stock details passed in request");
        watchList.getStocks().removeAll(deletedStocks);
        saveWatchList(watchList);
        MarketDetailsRequest request = MarketDetailsRequest.get();
        deletedStocks.stream().map(WatchlistStock::getStock).forEach(stock->{
            request.removeInstrument(MarketDetailsRequest.InstrumentDetails.of(stock.getInstrumentToken(),stock.getExchange(),stock.getName()));

        });
        exchangeClient.subScribeInstruments(request);
    }

    @Transactional
    public void addStockInWatchList(String userId, List<Long> stocksIdsToAdd){
        long id;
        if((id =CommonValidations.getNumber(userId,Long.class))==0)
            throw new BadRequestAlertException("Invalid User details", "Watchlist service", "Not valid user passed in request");

       var watchList = getWatchList(id).orElseGet(()->new WatchList(id));
        List<WatchlistStock> watchListStocks = watchList.getId() !=null ?
                watchListStockService.getAllWatchlistStock(watchList.getId()): new ArrayList<>();
       var updatedStocks = watchListStocks
                .stream()
                .filter(stock->stocksIdsToAdd.contains(stock.getStock().getId()))
                .map(WatchlistStock::getStock)
                .map(Stock::getId)
                .toList();
       stocksIdsToAdd.removeAll(updatedStocks);
       watchList.setStocks(getWatchListStock(watchList,watchListStocks,stocksIdsToAdd));
       saveWatchList(watchList);
    }

    @Transactional
    public void updateWatchlist(String userId, List<Long> updatedStockIdList){
        long id;
        if((id =CommonValidations.getNumber(userId,Long.class))==0)
            throw new BadRequestAlertException("Invalid User details", "Watchlist service", "Not valid user passed in request");

        if(updatedStockIdList==null || updatedStockIdList.isEmpty())
            throw new BadRequestAlertException("Invalid stock details", "Watchlist service", "Not valid stock details in request");

        var watchList = getWatchList(id)
                .orElseThrow(()->
                        new BadRequestAlertException("Invalid User details", "Watchlist service", "No watchlist is associated with user"));

        if(watchList.getStocks()==null || watchList.getStocks().isEmpty() || watchList.getStocks().size() != updatedStockIdList.size())
            throw new BadRequestAlertException("Invalid stock list", "Watchlist service", "Not valid stock list for re arranging watchlist");
        //watchList.setStocks(getWatchListStock(watchList,updatedStockIdList));
        CustomSorter.sortById(watchList.getStocks(), updatedStockIdList, WatchlistStock::getId, Optional.of(WatchlistStock::setOrderNum));
        saveWatchList(watchList);
        //return getWatchListDTO(saveWatchList(watchList));
    }

    private WatchListDTO getWatchListDTO(WatchList watchList){
        WatchListDTO watchListDTO = new WatchListDTO();
        modelMapper.map(watchList,watchListDTO);
//        watchListDTO.getWatchListStocks()
//                .forEach(stock-> {
//                    stock.getStock().setQuotes((MarketQuotes) redisService.getStockValue(String.valueOf(stock.getStock().getInstrumentToken())));
//                    stock.getStock().updatePrice();
//                });

        return watchListDTO;
    }

    private List<WatchlistStock> getWatchListStock(WatchList watchList,List<WatchlistStock> watchlistStocks,List<Long> stockIdList){
       if(stockIdList.isEmpty())
           return new ArrayList<>();
        MarketDetailsRequest request = MarketDetailsRequest.get();
        stockService.getStocks(stockIdList).forEach(
                stock->{
                    WatchlistStock watchlistStock = new WatchlistStock();
                    watchlistStock.setStock(stock);
                    watchlistStock.setWatchList(watchList);
                    watchlistStocks.add(watchlistStock);
                    request.addInstrument(MarketDetailsRequest.InstrumentDetails.of(stock.getInstrumentToken(),stock.getExchange(),stock.getName()));
        });
        exchangeClient.subScribeInstruments(request);
        return watchlistStocks;
    }


    private Optional<WatchList> getWatchList(long userId){
        return watchListRepository.findByUserId(userId);
    }
    private void saveWatchList(WatchList watchList){
        watchListRepository.save(watchList);
         watchListStockService.saveWatchlist(watchList.getStocks());
    }


    public List<WatchListStockDTO> mapQuotesToDTO(List<WatchListStockDTO> watchListStocks){
        MarketDetailsRequest request =new MarketDetailsRequest();
        var filteredList =watchListStocks.stream().map(WatchListStockDTO::getStock)
                .map(stock->{
                    stock.setQuotes((MarketQuotes) redisService.getStockValue(String.valueOf(stock.getInstrumentToken())));
                    stock.updatePrice();
                    request.addInstrument(MarketDetailsRequest.InstrumentDetails.of(stock.getInstrumentToken(),stock.getExchange(),stock.getTradingSymbol()));
                    return stock;
                }).filter(stock->stock.getQuotes() ==null || stock.getLastPrice()==0.0)
                .toList();
        var marketResponse = exchangeClient.getQuotesFromMarketList(request);
        filteredList.forEach(stockDTO->{
            MarketQuotes quotes = marketResponse.get(String.valueOf(stockDTO.getInstrumentToken()));
            stockDTO.setQuotes(quotes);
            stockDTO.updatePrice();
        });
        return watchListStocks;
    }
}
