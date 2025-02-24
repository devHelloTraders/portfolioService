package com.traders.portfolio.service;

import com.traders.common.utils.CommonValidations;
import com.traders.common.utils.DateTimeUtil;
import com.traders.portfolio.domain.*;
import com.traders.portfolio.exception.BadRequestAlertException;
import com.traders.portfolio.repository.DepositRequestRepository;
import com.traders.portfolio.repository.WalletRepository;
import com.traders.portfolio.repository.WalletTransactionRepository;
import com.traders.portfolio.repository.WithdrawRequestRepository;
import com.traders.portfolio.service.dto.FundDepositRequest;
import com.traders.portfolio.service.dto.FundWithdrawRequest;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WalletService {
    private final DepositRequestRepository depositRequestRepository;
    private final WithdrawRequestRepository withdrawRequestRepository;
    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;

    public WalletService(DepositRequestRepository depositRequestRepository, WithdrawRequestRepository withdrawRequestRepository, WalletRepository walletRepository, WalletTransactionRepository walletTransactionRepository) {
        this.depositRequestRepository = depositRequestRepository;
        this.withdrawRequestRepository = withdrawRequestRepository;
        this.walletRepository = walletRepository;
        this.walletTransactionRepository = walletTransactionRepository;
    }

    public void addDepositRequest(String userId, FundDepositRequest fundDepositRequest)  {
        long id=getUserId(userId);
        DepositRequest depositRequest = new DepositRequest();
        depositRequest.setAmount(fundDepositRequest.amount());
        depositRequest.setWalletId(getWalletFor(id));
        depositRequest.setRequestDateTime(DateTimeUtil.getCurrentDateTime());
        depositRequest.setRequestStatus(WalletRequestStatus.PENDING);
        depositRequestRepository.save(depositRequest);
    }

    public void addWithdrawRequest(String userId, FundWithdrawRequest fundWithdrawRequest) {
        long id=getUserId(userId);
        WithdrawRequest withdrawRequest = new WithdrawRequest();
        withdrawRequest.setPaymentMethod(fundWithdrawRequest.paymentMethod());
        withdrawRequest.setAmount(fundWithdrawRequest.amount());
        withdrawRequest.setWalletId(getWalletFor(id));
        withdrawRequest.setRequestDateTime(DateTimeUtil.getCurrentDateTime());
        withdrawRequest.setRequestStatus(WalletRequestStatus.PENDING);
        withdrawRequestRepository.save(withdrawRequest);
    }

    public Double getCurrentBalance(Long userId){
        Wallet wallet=getWalletFor(userId);
        return wallet.getBalance();
    }

    @Transactional
    public void updateCurrentBalance(Long userId,
                                     Double addOnBalance,
                                     WalletTransactionType transactionType,
                                     String transactionRemarks){
        Wallet wallet=getWalletFor(userId);

        WalletTransaction walletTransaction=new WalletTransaction();
        walletTransaction.setAmount(addOnBalance);
        walletTransaction.setTransactionType(transactionType);
        walletTransaction.setWalletId(wallet);
        walletTransaction.setRemarks(transactionRemarks);
        walletTransaction.setCreatedDateTime(DateTimeUtil.getCurrentDateTime());
        walletTransactionRepository.save(walletTransaction);

        wallet.setBalance(wallet.getBalance()+walletTransaction.getAmount());
        walletRepository.save(wallet);
    }

    public Page<WithdrawRequest> getWithdrawRequests(String userId, int page, int size){
        long id=getUserId(userId);
        Wallet wallet=getWalletFor(id);
        PageRequest pageRequest=PageRequest.of(page, size);
        return withdrawRequestRepository.findByWalletIdOrderByRequestDateTimeDesc(wallet,pageRequest);
    }

    public Page<DepositRequest> getDepositRequests(String userId, int page, int size){
        long id=getUserId(userId);
        Wallet wallet=getWalletFor(id);
        PageRequest pageRequest=PageRequest.of(page, size);
        return depositRequestRepository.findByWalletIdOrderByRequestDateTimeDesc(wallet,pageRequest);
    }


    private Wallet getWalletFor(Long userId){
        Optional<Wallet> wallet=walletRepository.findByUserId(userId);
        if(wallet.isEmpty())
            throw new BadRequestAlertException("Wallet not found", "Wallet Service", "Not valid user.");
        return wallet.get();
    }

    private long getUserId(String userId){
        long id;
        if((id = CommonValidations.getNumber(userId,Long.class))==0)
            throw new BadRequestAlertException("Invalid User details", "Transaction Service", "Not valid user passed in request");
        return id;
    }
}
