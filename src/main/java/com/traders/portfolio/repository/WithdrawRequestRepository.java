package com.traders.portfolio.repository;

import com.traders.portfolio.domain.WithdrawRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WithdrawRequestRepository extends JpaRepository<WithdrawRequest, Long> {
}
