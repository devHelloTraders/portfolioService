package com.traders.portfolio.service;

import com.traders.common.model.MarketQuotes;
import com.traders.portfolio.domain.WatchlistStock;
import com.traders.portfolio.repository.WatchListStockRepository;
import com.traders.portfolio.service.dto.WatchListStockDTO;
import com.traders.portfolio.service.specification.JPAFilterSpecification;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class WatchListStockService {

    private final WatchListStockRepository watchListStockRepository;
    private final RedisService redisService;
    private final ModelMapper modelMapper;
    public WatchListStockService(WatchListStockRepository watchListStockRepository, RedisService redisService, ModelMapper modelMapper) {
        this.watchListStockRepository = watchListStockRepository;
        this.redisService = redisService;
        this.modelMapper = modelMapper;
    }



    public Page<WatchListStockDTO> getFilterWatchListStocks( Map<String, Object> filters, Pageable pageable) {

        Specification<WatchlistStock> specification = JPAFilterSpecification.setFilter(filters);
        return watchListStockRepository.findAll(specification, pageable).map(this::translateToDto);
    }

    private WatchListStockDTO translateToDto(WatchlistStock stock){
        WatchListStockDTO stockDTO = new WatchListStockDTO();
        modelMapper.map(stock,stockDTO);
        stockDTO.getStock().setQuotes((MarketQuotes) redisService.getStockValue(String.valueOf(stock.getStock().getInstrumentToken())));
        stockDTO.getStock().updatePrice();
        return stockDTO;
    }
    public List<WatchlistStock> getAllWatchlistStock(long id){

        return watchListStockRepository.findByWatchList_Id(id);
    }
    public void saveWatchlist(List<WatchlistStock> watchlistStocks){
        if(watchlistStocks ==null || watchlistStocks.isEmpty())
            return;
        watchListStockRepository.saveAll(watchlistStocks);
    }
}
