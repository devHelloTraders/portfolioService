package com.traders.portfolio.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.traders.common.utils.DateTimeUtil;
import com.traders.portfolio.repository.TransactionRepository;
import com.traders.portfolio.service.dto.TradeRequest;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum OrderCategory {
    MARKET {
        @Override
        public List<Transaction> addTransaction(Double marginUsed,
                                                PortfolioStock portfolioStock,
                                                TradeRequest request,
                                                TransactionRepository transactionRepository) {
            List<Transaction> transactions = request.orderType().getTransactions(marginUsed, portfolioStock, request, transactionRepository);
            return transactions.stream().map(transaction -> {
                transaction.setTransactionStatus(TransactionStatus.COMPLETED);
                transaction.setOrderCategory(this);
                transaction.setCompletedTimestamp(DateTimeUtil.getCurrentDateTime());
                transaction.setExecutedPrice(request.askedPrice());
                return transactionRepository.save(transaction);
            }).collect(Collectors.toList());
        }
    },
    LIMIT {
        @Override
        public List<Transaction> addTransaction(Double marginUsed, PortfolioStock portfolioStock, TradeRequest request, TransactionRepository transactionRepository) {
            List<Transaction> transactions = request.orderType().getTransactions(marginUsed, portfolioStock, request, transactionRepository);
            return transactions.stream().map(transaction -> {
                transaction.setTransactionStatus(TransactionStatus.PENDING);
                transaction.setOrderCategory(this);
                return transactionRepository.save(transaction);
            }).collect(Collectors.toList());
        }
    },
    BRACKET_AT_MARKET {
        @Override
        public List<Transaction> addTransaction(Double marginUsed, PortfolioStock portfolioStock, TradeRequest request, TransactionRepository transactionRepository) {
            List<Transaction> transactions = MARKET.addTransaction(marginUsed, portfolioStock, request, transactionRepository);
            //createStopLossTransaction(transaction, request, transactionRepository);
            //createTargetTransaction(transaction, request, transactionRepository);
            return transactions;
        }
    },
    BRACKET_AT_LIMIT {
        @Override
        public List<Transaction> addTransaction(Double marginUsed, PortfolioStock portfolioStock, TradeRequest request, TransactionRepository transactionRepository) {
            List<Transaction> transactions = LIMIT.addTransaction(marginUsed, portfolioStock, request, transactionRepository);
            //createStopLossTransaction(transaction, request, transactionRepository);
            //createTargetTransaction(transaction, request, transactionRepository);
            return transactions;
        }
    },
    STOP_LOSS {
        @Override
        public List<Transaction> addTransaction(Double marginUsed, PortfolioStock portfolioStock, TradeRequest request, TransactionRepository transactionRepository) {
            /*Transaction transaction = createTransaction(request, this, TransactionStatus.PENDING, OrderType.SELL, marginUsed);
            return transactionRepository.save(transaction);*/
            return Collections.emptyList();
        }
    };

    abstract public List<Transaction> addTransaction(Double marginUsed, PortfolioStock portfolioStock, TradeRequest request, TransactionRepository transactionRepository);

    /*void createStopLossTransaction(Transaction parentTransaction, TradeRequest request, TransactionRepository transactionRepository) {
        Transaction stopLossTransaction = new Transaction();
        stopLossTransaction.setLotSize(request.lotSize());
        stopLossTransaction.setPrice(request.stopLossPrice() == null ? request.askedPrice() : request.stopLossPrice());
        stopLossTransaction.setTransactionStatus(TransactionStatus.PENDING);
        stopLossTransaction.setOrderCategory(STOP_LOSS);
        stopLossTransaction.setOrderType(OrderType.SELL);
        stopLossTransaction.setRequestTimestamp(LocalDateTime.now());
        stopLossTransaction.setParentTransaction(parentTransaction);
        transactionRepository.save(stopLossTransaction);
    }

    void createTargetTransaction(Transaction parentTransaction, TradeRequest request, TransactionRepository transactionRepository) {
        Transaction targetTransaction = new Transaction();
        targetTransaction.setLotSize(request.lotSize());
        targetTransaction.setPrice(request.targetPrice() == null ? request.askedPrice() : request.targetPrice());
        targetTransaction.setTransactionStatus(TransactionStatus.PENDING);
        targetTransaction.setOrderCategory(LIMIT);
        targetTransaction.setOrderType(OrderType.SELL);
        targetTransaction.setRequestTimestamp(LocalDateTime.now());
        targetTransaction.setParentTransaction(parentTransaction);
        transactionRepository.save(targetTransaction);
    }*/

}