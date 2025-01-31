package com.traders.portfolio.service.mapper;

import com.traders.portfolio.domain.WithdrawRequest;
import com.traders.portfolio.service.dto.WithdrawRequestDto;
import org.springframework.stereotype.Service;

@Service
public class WithdrawRequestMapper {

    public WithdrawRequestDto toWithdrawRequestDto(WithdrawRequest withdrawRequest) {
        WithdrawRequestDto withdrawRequestDto = new WithdrawRequestDto();
        withdrawRequestDto.setWithdrawRequestId(withdrawRequest.getWithdrawRequestId());
        withdrawRequestDto.setAmount(withdrawRequest.getAmount());
        withdrawRequestDto.setRequestStatus(withdrawRequest.getRequestStatus().name());
        withdrawRequestDto.setRequestedDateTime(withdrawRequest.getRequestDateTime());
        withdrawRequestDto.setPaymentMethod(withdrawRequest.getPaymentMethod().name());
        withdrawRequestDto.setUpiId(withdrawRequest.getUpiId());
        withdrawRequestDto.setAccountHolderName(withdrawRequest.getAccountHolderName());
        withdrawRequestDto.setAccountNumber(withdrawRequest.getAccountNumber());
        withdrawRequestDto.setIfsc(withdrawRequest.getIfsc());
        return withdrawRequestDto;
    }
}
