package com.traders.portfolio.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.traders.portfolio.repository.TransactionRepository;
import com.traders.portfolio.service.dto.TradeRequest;

import java.time.LocalDateTime;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum OrderCategory {
    MARKET {
        @Override
        public Transaction addTransaction(Double marginUsed,PortfolioStock portfolioStock, TradeRequest request, TransactionRepository transactionRepository) {
            Transaction transaction = createTransaction(request, this, TransactionStatus.COMPLETED, request.orderType(),marginUsed);
            transaction.setPortfolioStock(portfolioStock);
            transaction.setExecutedPrice(request.askedPrice());
            transaction.setCompletedTimestamp(LocalDateTime.now());
            return transactionRepository.save(transaction);
        }
    },
    LIMIT {
        @Override
        public Transaction addTransaction(Double marginUsed,PortfolioStock portfolioStock, TradeRequest request, TransactionRepository transactionRepository) {
            Transaction transaction = createTransaction(request, this, TransactionStatus.PENDING, request.orderType(),marginUsed);
            transaction.setPortfolioStock(portfolioStock);
            return transactionRepository.save(transaction);
        }
    },
    BRACKET_AT_MARKET {
        @Override
        public Transaction addTransaction(Double marginUsed,PortfolioStock portfolioStock, TradeRequest request, TransactionRepository transactionRepository) {
            Transaction transaction = MARKET.addTransaction(marginUsed,portfolioStock, request, transactionRepository);
            createStopLossTransaction(transaction, request, transactionRepository);
            createTargetTransaction(transaction, request, transactionRepository);
            return transaction;
        }
    },
    BRACKET_AT_LIMIT {
        @Override
        public Transaction addTransaction(Double marginUsed,PortfolioStock portfolioStock, TradeRequest request, TransactionRepository transactionRepository) {
            Transaction transaction = LIMIT.addTransaction(marginUsed,portfolioStock, request, transactionRepository);
            createStopLossTransaction(transaction, request, transactionRepository);
            createTargetTransaction(transaction, request, transactionRepository);
            return transaction;
        }
    },
    STOP_LOSS {
        @Override
        public Transaction addTransaction(Double marginUsed,PortfolioStock portfolioStock, TradeRequest request, TransactionRepository transactionRepository) {
            Transaction transaction = createTransaction(request, this, TransactionStatus.PENDING, OrderType.SELL,marginUsed);
            return transactionRepository.save(transaction);
        }
    };

    abstract public Transaction addTransaction(Double marginUsed,PortfolioStock portfolioStock, TradeRequest request, TransactionRepository transactionRepository);

    Transaction createTransaction(TradeRequest request, OrderCategory orderCategory, TransactionStatus status, OrderType orderType,Double margin) {
        Transaction transaction = new Transaction();
        transaction.setLotSize(request.lotSize());
        transaction.setPrice(request.askedPrice());
        transaction.setTransactionStatus(status);
        transaction.setOrderCategory(orderCategory);
        transaction.setOrderType(orderType);
        transaction.setRequestTimestamp(LocalDateTime.now());
        transaction.setMargin(margin);
        return transaction;
    }

   void createStopLossTransaction(Transaction parentTransaction, TradeRequest request, TransactionRepository transactionRepository) {
        Transaction stopLossTransaction = new Transaction();
        stopLossTransaction.setLotSize(request.lotSize());
        stopLossTransaction.setPrice(request.stopLossPrice()== null ? request.askedPrice() :  request.stopLossPrice());
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
        targetTransaction.setPrice(request.targetPrice()== null ? request.askedPrice() :  request.targetPrice());
        targetTransaction.setTransactionStatus(TransactionStatus.PENDING);
        targetTransaction.setOrderCategory(LIMIT);
        targetTransaction.setOrderType(OrderType.SELL);
        targetTransaction.setRequestTimestamp(LocalDateTime.now());
        targetTransaction.setParentTransaction(parentTransaction);
        transactionRepository.save(targetTransaction);
    }

}