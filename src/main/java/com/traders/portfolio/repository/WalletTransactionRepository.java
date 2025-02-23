package com.traders.portfolio.repository;

import com.traders.portfolio.domain.Wallet;
import com.traders.portfolio.domain.WalletTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {
    Page<WalletTransaction> findByWalletId(Wallet wallet, Pageable pageable);
}
