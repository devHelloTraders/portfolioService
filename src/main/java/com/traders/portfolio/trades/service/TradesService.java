package com.traders.portfolio.trades.service;

import com.traders.portfolio.trades.dto.ActiveTradesResponseDTO;
import com.traders.portfolio.trades.dto.ClosedTradesResponseDTO;
import com.traders.portfolio.trades.dto.PendingTradesResponseDTO;
import com.traders.portfolio.trades.repository.TradesRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TradesService {
    private final TradesRepository repository;

    public TradesService(TradesRepository repository) {
        this.repository = repository;
    }

    public List<ActiveTradesResponseDTO> getAllActiveTrades(int size, int page, Map<String, Object> filters) {
        return repository.getActiveTrades(size, page, filters);
    }

    public List<ClosedTradesResponseDTO> getClosedTrades(int size,int page, Map<String, Object> filters) {
        return repository.getClosedTrades(size, page, filters);
    }

    public List<PendingTradesResponseDTO> getPendingTrades(int size, int page, Map<String, Object> filters) {
        return repository.getPendingTrades(size, page, filters);
    }
}
