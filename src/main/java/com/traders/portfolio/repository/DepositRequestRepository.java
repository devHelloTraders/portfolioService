package com.traders.portfolio.repository;

import com.traders.portfolio.domain.DepositRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepositRequestRepository extends JpaRepository<DepositRequest, Long> {
}
