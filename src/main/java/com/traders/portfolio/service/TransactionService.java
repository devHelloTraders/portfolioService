package com.traders.portfolio.service;

import com.traders.common.utils.CommonValidations;
import com.traders.portfolio.domain.Transaction;
import com.traders.portfolio.domain.TransactionStatus;
import com.traders.portfolio.exception.BadRequestAlertException;
import com.traders.portfolio.repository.TransactionRepository;
import com.traders.portfolio.service.dto.TransactionDTO;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final PortfolioService portfolioService;
    private final ModelMapper modelMapper;

    public TransactionService(TransactionRepository transactionRepository, PortfolioService portfolioService, ModelMapper modelMapper) {
        this.transactionRepository = transactionRepository;
        this.portfolioService = portfolioService;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public List<TransactionDTO> getTransactions(@NotNull String userId){
        long id;
        if((id = CommonValidations.getNumber(userId,Long.class))==0)
            throw new BadRequestAlertException("Invalid User details", "Transaction Service", "Not valid user passed in request");

        return getTransactionDTOList(getTransaction(id));
    }

    private List<Transaction> getTransaction(long id){
        return transactionRepository.findByCreatedBy(String.valueOf(id));
    }
    private List<TransactionDTO> getTransactionDTOList(List<Transaction> transaction){
        List<TransactionDTO> transactionDetailsList = new ArrayList<>();
        transaction.forEach(txn-> {
            TransactionDTO transactionDetails = new TransactionDTO();
            modelMapper.map(txn, transactionDetails);
            transactionDetailsList.add(transactionDetails);
        });
        return transactionDetailsList;
    }

    private Transaction getTransactionFromDTO(TransactionDTO transactionDTO){
        Transaction transaction = new Transaction();
        modelMapper.map(transactionDTO,transaction);
        return transaction;
    }
    @Transactional
    public void addTransaction(String userId,@NotNull TransactionDTO transactionDTO){

        long id;
        if((id =CommonValidations.getNumber(userId,Long.class))==0)
            throw new BadRequestAlertException("Invalid User details", "Transaction Service", "Not valid user passed in request");

        Transaction transaction = getTransactionFromDTO(transactionDTO);
        transaction.getOrderType().setQuantity(transactionDTO.getCompletedQuantity());
       // saveTransaction(transaction);
        portfolioService.addTransactionToPortfolio(id, transaction);
    }

    public void updateTransactionStatus(@Valid  Long transactionId, @NotNull TransactionStatus status){
        Transaction transaction = getTransactionById(transactionId).orElseThrow(()->
         new BadRequestAlertException("Invalid Transaction details", "Transaction Service", "Not valid Transaction id passed in request"));
        transaction.setCompletedTimestamp(status.completedTime());
        transaction.setCompletedQuantity(status.getQuantity());
        saveTransaction(transaction);
    }

    private Optional<Transaction> getTransactionById(Long transactionId){
        return transactionRepository.findById(transactionId);
    }

    private Transaction saveTransaction(Transaction transaction){
        return transactionRepository.save(transaction);
    }
}
