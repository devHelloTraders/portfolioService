package com.traders.portfolio.repository;

import com.traders.portfolio.domain.OrderType;
import com.traders.portfolio.domain.PortfolioStock;
import com.traders.portfolio.domain.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Long>, JpaSpecificationExecutor<Transaction> {

    Page<Transaction> findByCreatedBy (String userId, Pageable pageable);
    Page<Transaction> findAll(Pageable pageable);


    @Modifying
    @Query("UPDATE Transaction SET deleteflag = 1 WHERE portfolioStock=:portfolioStock")
    void closeTransaction(@Param("portfolioStock") PortfolioStock portfolioStock);

    List<Transaction> findAllByPortfolioStockAndQtyGreaterThanAndDeleteflagAndOrderType(PortfolioStock portfolioStock,
                                                                                        Double qty,
                                                                                        int deleteflag, OrderType orderType);
}
