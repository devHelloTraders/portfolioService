package com.traders.portfolio.repository;

import com.traders.portfolio.domain.Stock;
import com.traders.portfolio.domain.WatchList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock,Long> {

    List<Stock> findAllByIdIn (List<Long> ids);
}
