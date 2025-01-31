package com.traders.portfolio.service;

import com.traders.portfolio.domain.Stock;
import com.traders.portfolio.exception.BadRequestAlertException;
import com.traders.portfolio.repository.StockRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockService {

    private final StockRepository stockRepository;

    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Transactional
    public List<Stock> getStocks(List<Long> stockIds){
        if(stockIds == null || stockIds.isEmpty())
            throw new BadRequestAlertException("Invalid Stock id details", "Stock Service service", "Not a valid stock details");
        return stockRepository.findAllByIdIn(stockIds);
    }
    @Transactional
    public Stock getStock(Long stockId){
        if(stockId == null || stockId<=0)
            throw new BadRequestAlertException("Invalid Stock id details", "Stock Service service", "Not a valid stock details");
        return stockRepository.findById(stockId).orElseThrow
                (()->new BadRequestAlertException("Invalid Stock id details", "Stock Service service", "Not a valid stock details"));
    }
}
