package com.traders.portfolio.service;

import com.traders.common.utils.CommonValidations;
import com.traders.common.utils.DateTimeUtil;
import com.traders.portfolio.domain.Transaction;
import com.traders.portfolio.domain.TransactionStatus;
import com.traders.portfolio.exception.BadRequestAlertException;
import com.traders.portfolio.repository.TransactionRepository;
import com.traders.portfolio.service.dto.*;
import com.traders.portfolio.service.specification.JPAFilterSpecification;
import jakarta.transaction.Transactional;
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
    public List<Long> addTransaction(String userId,@NotNull TradeRequest tradeRequest){

        long id;
        if((id =CommonValidations.getNumber(userId,Long.class))==0)
            throw new BadRequestAlertException("Invalid User details", "Transaction Service", "Not valid user passed in request");
        return portfolioService.addTransactionToPortfolio(id, tradeRequest);
    }
    @Transactional
    public void updateTransactionStatus( @NotNull TransactionUpdateRecord updateDTO){
        Transaction transaction = getTransactionById(updateDTO.id()).orElseThrow(()->
         new BadRequestAlertException("Invalid Transaction details", "Transaction Service", "Not valid Transaction id passed in request"));
        if(transaction.getTransactionStatus()==TransactionStatus.COMPLETED || transaction.getTransactionStatus()==TransactionStatus.CANCELLED)
           throw new BadRequestAlertException("Invalid Transaction State", "Transaction Service", "You cant modify Completed and Cancelled Transactions");
        updateDTO.transactionStatus().setExecutedPrice(updateDTO.price());
        transaction.setCompletedTimestamp(DateTimeUtil.getCurrentDateTime());
        transaction.setExecutedPrice(updateDTO.price());
        transaction.setTransactionStatus(updateDTO.transactionStatus());
        transaction.getPortfolioStock().addQuantity(transaction.getQty(),transaction.getExecutedPrice());
        saveTransaction(transaction);
    }

    public void cancelTransaction(@NotNull Long userId,@NotNull CancelTransactionRecord cancelTransactionRecord){
        Transaction transaction = getTransactionById(cancelTransactionRecord.id()).orElseThrow(()->
                new BadRequestAlertException("Invalid Transaction details", "Transaction Service", "Not valid Transaction id passed in request"));
        if(transaction.getTransactionStatus()==TransactionStatus.COMPLETED || transaction.getTransactionStatus()==TransactionStatus.CANCELLED)
            throw new BadRequestAlertException("Invalid Transaction State", "Transaction Service", "You cant modify Completed and already Cancelled Transactions");
        //TODO: CHECK password of user.
        transaction.setTransactionStatus(TransactionStatus.CANCELLED);
        transaction.setCompletedTimestamp(DateTimeUtil.getCurrentDateTime());
        transaction.setDeleteflag(1);
        saveTransaction(transaction);
    }

    public void updatePendingTransaction(@NotNull Long userId,@NotNull UpdatePendingTransactionRecord updateDTO){
        Transaction transaction = getTransactionById(updateDTO.transactionId()).orElseThrow(()->
                new BadRequestAlertException("Invalid Transaction details", "Transaction Service", "Not valid Transaction id passed in request"));
        if(transaction.getTransactionStatus()==TransactionStatus.COMPLETED || transaction.getTransactionStatus()==TransactionStatus.CANCELLED)
            throw new BadRequestAlertException("Invalid Transaction State", "Transaction Service", "You cant modify Completed and Cancelled Transactions");
        transaction.setRequestTimestamp(DateTimeUtil.getCurrentDateTime());
        transaction.setPrice(updateDTO.price());
        transaction.setTransactionStatus(TransactionStatus.PENDING);
        Double scriptLotSize=transaction.getPortfolioStock().getStock().getLotSize();
        transaction.setQty(updateDTO.lotSize()*scriptLotSize);  //To convert it to qty.
        transaction.setLastModifiedBy(String.valueOf(userId));
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
