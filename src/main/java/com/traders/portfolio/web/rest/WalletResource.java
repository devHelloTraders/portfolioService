package com.traders.portfolio.web.rest;

import com.traders.portfolio.domain.DepositRequest;
import com.traders.portfolio.domain.WithdrawRequest;
import com.traders.portfolio.service.WalletService;
import com.traders.portfolio.service.dto.DepositRequestDto;
import com.traders.portfolio.service.dto.FundDepositRequest;
import com.traders.portfolio.service.dto.FundWithdrawRequest;
import com.traders.portfolio.service.dto.WithdrawRequestDto;
import com.traders.portfolio.service.mapper.DepositRequestMapper;
import com.traders.portfolio.service.mapper.WithdrawRequestMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/wallet")
public class WalletResource {

    private final WalletService walletService;
    private final DepositRequestMapper depositRequestMapper;
    private final WithdrawRequestMapper withdrawRequestMapper;

    public WalletResource(WalletService walletService, DepositRequestMapper depositRequestMapper, WithdrawRequestMapper withdrawRequestMapper) {
        this.walletService = walletService;
        this.depositRequestMapper = depositRequestMapper;
        this.withdrawRequestMapper = withdrawRequestMapper;
    }

    @PostMapping("/add-deposit-request")
    public ResponseEntity<HttpStatus> addDepositRequest(@RequestBody FundDepositRequest fundDepositRequest) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        walletService.addDepositRequest(userId, fundDepositRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(HttpStatus.CREATED);
    }

    @PostMapping("/add-withdraw-request")
    public ResponseEntity<HttpStatus> addWithdrawRequest(@RequestBody FundWithdrawRequest fundWithdrawRequest) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        walletService.addWithdrawRequest(userId, fundWithdrawRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(HttpStatus.CREATED);
    }

    @GetMapping("/withdraw-requests")
    public List<WithdrawRequestDto> getWithdrawRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        Page<WithdrawRequest> withdrawRequests = walletService.getWithdrawRequests(userId, page, size);
        return withdrawRequests.getContent().stream().map(withdrawRequestMapper::toWithdrawRequestDto).collect(Collectors.toList());
    }

    @GetMapping("/deposit-requests")
    public List<DepositRequestDto> getDepositRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        String userId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        Page<DepositRequest> withdrawRequests = walletService.getDepositRequests(userId, page, size);
        return withdrawRequests.getContent().stream().map(depositRequestMapper::toDepositRequestDto).collect(Collectors.toList());
    }
}
