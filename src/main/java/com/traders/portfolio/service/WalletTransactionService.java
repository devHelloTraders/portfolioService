package com.traders.portfolio.service;

import com.traders.portfolio.domain.WalletTransaction;
import com.traders.portfolio.repository.WalletTransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class WalletTransactionService {
    private final WalletTransactionRepository walletTransactionRepository;
    private final Logger logger= LoggerFactory.getLogger(WalletTransactionService.class);

    public WalletTransactionService(WalletTransactionRepository walletTransactionRepository) {
        this.walletTransactionRepository = walletTransactionRepository;
    }

    public void addWalletTransaction(WalletTransaction walletTransaction) {
        try{
            if(walletTransaction!=null)
                walletTransactionRepository.save(walletTransaction);
        }catch (Exception e){
            logger.error(e.getMessage());
        }
    }
}
