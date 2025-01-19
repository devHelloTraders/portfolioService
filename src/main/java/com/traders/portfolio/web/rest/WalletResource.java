package com.traders.portfolio.web.rest;

import com.traders.portfolio.service.WalletService;
import com.traders.portfolio.service.dto.FundDepositRequest;
import com.traders.portfolio.service.dto.FundWithdrawRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallet")
public class WalletResource {

    private final WalletService walletService;

    public WalletResource(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping("/add-deposit-request")
    public ResponseEntity<HttpStatus> addDepositRequest(@RequestBody FundDepositRequest fundDepositRequest){
        String userId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        walletService.addDepositRequest(userId, fundDepositRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(HttpStatus.CREATED);
    }

    @PostMapping("/add-withdraw-request")
    public ResponseEntity<HttpStatus> addWithdrawRequest(@RequestBody FundWithdrawRequest fundWithdrawRequest){
        String userId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        walletService.addWithdrawRequest(userId, fundWithdrawRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(HttpStatus.CREATED);
    }
}
