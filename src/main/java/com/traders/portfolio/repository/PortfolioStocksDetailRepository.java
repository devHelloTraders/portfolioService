package com.traders.portfolio.repository;

import com.traders.portfolio.domain.PortfolioStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PortfolioStocksDetailRepository extends JpaRepository<PortfolioStock,Long> {

    @Modifying
    @Query("UPDATE PortfolioStock SET deleteflag = 1 WHERE id = :portfolioStockId")
    void closeInstrumentDeal(@Param("portfolioStockId") Long portfolioStockId);
}
