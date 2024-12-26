package com.traders.portfolio.service;

import com.traders.common.utils.CommonValidations;
import com.traders.portfolio.domain.Portfolio;
import com.traders.portfolio.domain.PortfolioStock;
import com.traders.portfolio.domain.Transaction;
import com.traders.portfolio.domain.TransactionStatus;
import com.traders.portfolio.exception.BadRequestAlertException;
import com.traders.portfolio.repository.TransactionRepository;
import com.traders.portfolio.service.dto.TradeRequest;
import com.traders.portfolio.service.dto.TransactionDTO;
import com.traders.portfolio.service.specification.JPAFilterSpecification;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    public Page<TransactionDTO> getTransactions(@NotNull String userId,Pageable pageable){
        long id;
        if((id = CommonValidations.getNumber(userId,Long.class))==0)
            throw new BadRequestAlertException("Invalid User details", "Transaction Service", "Not valid user passed in request");

        return getTransaction(id,pageable);
    }

    private Page<TransactionDTO> getTransaction(long id, Pageable pageable){
        return transactionRepository.findByCreatedBy(String.valueOf(id),pageable).map(this::translateToDto);
    }

    public Page<TransactionDTO> getAllTransaction(Pageable pageable){
        return transactionRepository.findAll(pageable).map(this::translateToDto);
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

    private TransactionDTO translateToDto(Transaction transaction) {
        TransactionDTO transactionDetails = new TransactionDTO();
        modelMapper.map(transaction, transactionDetails);
        return transactionDetails;

    }

    private Transaction getTransactionFromDTO(TransactionDTO transactionDTO){
        Transaction transaction = new Transaction();
        modelMapper.map(transactionDTO,transaction);
        return transaction;
    }
    @Transactional
    public void addTransaction(String userId,@NotNull TradeRequest tradeRequest){

        long id;
        if((id =CommonValidations.getNumber(userId,Long.class))==0)
            throw new BadRequestAlertException("Invalid User details", "Transaction Service", "Not valid user passed in request");
        portfolioService.addTransactionToPortfolio(id, tradeRequest);
    }
    @Transactional
    public void updateTransactionStatus(@Valid  Long transactionId, @NotNull TransactionStatus status){
        Transaction transaction = getTransactionById(transactionId).orElseThrow(()->
         new BadRequestAlertException("Invalid Transaction details", "Transaction Service", "Not valid Transaction id passed in request"));
        if(transaction.getTransactionStatus()==TransactionStatus.COMPLETED || transaction.getTransactionStatus()==TransactionStatus.CANCELLED)
           throw new BadRequestAlertException("Invalid Transaction State", "Transaction Service", "You cant modify Completed and Cancelled Transactions");

        transaction.setCompletedTimestamp(status.completedTime());
        transaction.setExecutedPrice(status.getCompletedPrice());
        transaction.setTransactionStatus(status);
        transaction.getPortfolioStock().addQuantity(transaction.getLotSize(),transaction.getExecutedPrice());
        saveTransaction(transaction);
    }

    private Optional<Transaction> getTransactionById(Long transactionId){
        return transactionRepository.findById(transactionId);
    }

    private Transaction saveTransaction(Transaction transaction){
        return transactionRepository.save(transaction);
    }


    public Page<TransactionDTO> getFilteredTransactions(String userId, Map<String, Object> filters, Pageable pageable) {
        if(userId !=null)
            filters.put("createdBy", userId);
        Specification<Transaction> specification = JPAFilterSpecification.setFilter(filters);
        return transactionRepository.findAll(specification, pageable).map(this::translateToDto);
    }
}
