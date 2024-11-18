package com.traders.portfolio.service;

import com.traders.common.utils.CommonValidations;
import com.traders.portfolio.domain.Stock;
import com.traders.portfolio.domain.WatchList;
import com.traders.portfolio.domain.WatchlistStock;
import com.traders.portfolio.exception.BadRequestAlertException;
import com.traders.portfolio.repository.WatchListRepository;
import com.traders.portfolio.service.dto.WatchListDTO;
import com.traders.portfolio.utils.CustomSorter;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class WatchListService {

    private final WatchListRepository watchListRepository;
    private final RedisService redisService;
    private final StockService stockService;
    private final ModelMapper modelMapper;

    public WatchListService(WatchListRepository watchListRepository, RedisService redisService, StockService stockService, ModelMapper modelMapper) {
        this.watchListRepository = watchListRepository;
        this.redisService = redisService;
        this.stockService = stockService;
        this.modelMapper = modelMapper;
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

        var deletedStocks = watchList.getStocks()
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
    }

    @Transactional
    public WatchListDTO addStockInWatchList(String userId, List<Long> stocksIdsToAdd){
        long id;
        if((id =CommonValidations.getNumber(userId,Long.class))==0)
            throw new BadRequestAlertException("Invalid User details", "Watchlist service", "Not valid user passed in request");

       var watchList = getWatchList(id).orElseGet(()->new WatchList(id));

       var updatedStocks = watchList.getStocks()
                .stream()
                .filter(stock->stocksIdsToAdd.contains(stock.getId()))
                .map(WatchlistStock::getStock)
                .map(Stock::getId)
                .toList();
       stocksIdsToAdd.removeAll(updatedStocks);
       watchList.getStocks().addAll(getWatchListStock(watchList,stocksIdsToAdd));
       return getWatchListDTO(saveWatchList(watchList));
    }

    @Transactional
    public WatchListDTO updateWatchlist(String userId, List<Long> updatedStockIdList){
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
        return getWatchListDTO(saveWatchList(watchList));
    }

    private WatchListDTO getWatchListDTO(WatchList watchList){
        WatchListDTO watchListDTO = new WatchListDTO();
        modelMapper.map(watchList,watchListDTO);
        watchListDTO.getWatchListStocks()
                .forEach(stock->stock.setCurrentPrice(redisService.getDoubleValue(stock.getStock().getCurrentPriceKey())));

        return watchListDTO;
    }

    private List<WatchlistStock> getWatchListStock(WatchList watchList,List<Long> stockIdList){
        List<WatchlistStock> watchlistStocks = new ArrayList<>();
        stockService.getStocks(stockIdList).forEach(
                stock->{
                    WatchlistStock watchlistStock = new WatchlistStock();
                    watchlistStock.setStock(stock);
                    watchlistStock.setWatchList(watchList);
                    watchlistStocks.add(watchlistStock);
        });
        return watchlistStocks;
    }


    private Optional<WatchList> getWatchList(long userId){
        return watchListRepository.findByUserId(userId);
    }
    private WatchList saveWatchList(WatchList watchList){
        return watchListRepository.save(watchList);
    }
}
