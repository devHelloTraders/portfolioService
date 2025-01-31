package com.traders.portfolio.repository;

import com.traders.portfolio.domain.DepositRequest;
import com.traders.portfolio.domain.Wallet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepositRequestRepository extends JpaRepository<DepositRequest, Long> {

    Page<DepositRequest> findByWalletIdOrderByRequestDateTimeDesc(Wallet wallet, Pageable pageable);
}
