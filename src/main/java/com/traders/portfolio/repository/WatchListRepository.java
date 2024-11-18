package com.traders.portfolio.repository;

import com.traders.portfolio.domain.Portfolio;
import com.traders.portfolio.domain.WatchList;
import com.traders.portfolio.domain.WatchlistStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WatchListRepository extends JpaRepository<WatchList,Long> {

    Optional<WatchList> findByUserId (long userId);
}
