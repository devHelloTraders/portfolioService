package com.traders.portfolio.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.traders.portfolio.repository.TransactionRepository;
import com.traders.portfolio.service.dto.TradeRequest;

import java.time.LocalDateTime;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum OrderCategory {
    MARKET {
        @Override
        public Transaction addTransaction(PortfolioStock portfolioStock, TradeRequest request, TransactionRepository transactionRepository) {
            Transaction transaction = createTransaction(request, this, TransactionStatus.COMPLETED, request.orderType());
            transaction.setPortfolioStock(portfolioStock);
            transaction.setExecutedPrice(request.price());
            return transactionRepository.save(transaction);
        }
    },
    LIMIT {
        @Override
        public Transaction addTransaction(PortfolioStock portfolioStock, TradeRequest request, TransactionRepository transactionRepository) {
            Transaction transaction = createTransaction(request, this, TransactionStatus.PENDING, request.orderType());
            transaction.setPortfolioStock(portfolioStock);
            return transactionRepository.save(transaction);
        }
    },
    BRACKET_AT_MARKET {
        @Override
        public Transaction addTransaction(PortfolioStock portfolioStock, TradeRequest request, TransactionRepository transactionRepository) {
            Transaction transaction = MARKET.addTransaction(portfolioStock, request, transactionRepository);
            createStopLossTransaction(transaction, request, transactionRepository);
            createTargetTransaction(transaction, request, transactionRepository);
            return transaction;
        }
    },
    BRACKET_AT_LIMIT {
        @Override
        public Transaction addTransaction(PortfolioStock portfolioStock, TradeRequest request, TransactionRepository transactionRepository) {
            Transaction transaction = LIMIT.addTransaction(portfolioStock, request, transactionRepository);
            createStopLossTransaction(transaction, request, transactionRepository);
            createTargetTransaction(transaction, request, transactionRepository);
            return transaction;
        }
    },
    STOP_LOSS {
        @Override
        public Transaction addTransaction(PortfolioStock portfolioStock, TradeRequest request, TransactionRepository transactionRepository) {
            Transaction transaction = createTransaction(request, this, TransactionStatus.PENDING, OrderType.SELL);
            return transactionRepository.save(transaction);
        }
    };

    abstract public Transaction addTransaction(PortfolioStock portfolioStock, TradeRequest request, TransactionRepository transactionRepository);

    Transaction createTransaction(TradeRequest request, OrderCategory orderCategory, TransactionStatus status, OrderType orderType) {
        Transaction transaction = new Transaction();
        transaction.setLotSize(request.lotSize());
        transaction.setPrice(request.price());
        transaction.setTransactionStatus(status);
        transaction.setOrderCategory(orderCategory);
        transaction.setOrderType(orderType);
        transaction.setRequestTimestamp(LocalDateTime.now());
        return transaction;
    }

   void createStopLossTransaction(Transaction parentTransaction, TradeRequest request, TransactionRepository transactionRepository) {
        Transaction stopLossTransaction = new Transaction();
        stopLossTransaction.setLotSize(request.lotSize());
        stopLossTransaction.setPrice(request.stopLossPrice());
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
        targetTransaction.setPrice(request.targetPrice());
        targetTransaction.setTransactionStatus(TransactionStatus.PENDING);
        targetTransaction.setOrderCategory(LIMIT);
        targetTransaction.setOrderType(OrderType.SELL);
        targetTransaction.setRequestTimestamp(LocalDateTime.now());
        targetTransaction.setParentTransaction(parentTransaction);
        transactionRepository.save(targetTransaction);
    }

}