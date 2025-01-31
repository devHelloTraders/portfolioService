package com.traders.portfolio.service.mapper;

import com.traders.portfolio.domain.DepositRequest;
import com.traders.portfolio.service.dto.DepositRequestDto;
import org.springframework.stereotype.Service;

@Service
public class DepositRequestMapper {

    public DepositRequestDto toDepositRequestDto(DepositRequest depositRequest) {
        return new DepositRequestDto(
                depositRequest.getDepositRequestId(),
                depositRequest.getAmount(),
                depositRequest.getRequestDateTime(),
                depositRequest.getRequestStatus().name(),
                depositRequest.getApprovedDateTime()
        );
    }
}
