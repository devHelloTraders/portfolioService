package com.traders.portfolio.repository;

import com.traders.portfolio.domain.WatchlistStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WatchListStockRepository extends JpaRepository<WatchlistStock,Long>, JpaSpecificationExecutor<WatchlistStock> {

//    Optional<WatchList> findByUserId (long userId);
    List<WatchlistStock> findByWatchList_Id(Long watchListId);
}
