package com.traders.portfolio.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.traders.common.utils.DateTimeUtil;
import com.traders.portfolio.repository.TransactionRepository;
import com.traders.portfolio.service.dto.TradeRequest;
import com.traders.portfolio.service.dto.TransactionRequest;
import com.traders.portfolio.validations.AbstractConfigValidator;
import com.traders.portfolio.validations.exception.TradeValidationException;
import com.traders.portfolio.web.rest.errors.TradeValidationErrorCode;

import java.util.ArrayList;
import java.util.List;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum OrderType {
    BUY {
        @Override
        public Double getQuantity() {
            return quantity;
        }

        @Override
        public List<Transaction> getTransactions(Double balance, PortfolioStock portfolioStock,
                                                 TradeRequest request, TransactionRepository repository,boolean shortSell) {

            TransactionContext context = createTransactionContext(balance, portfolioStock, request);

            if (!shortSell) {
                return handleNormalBuy(context);
            } else {
                return handleShortCover(context, repository);
            }
        }
    },

    SELL {
        @Override
        public Double getQuantity() {
            return -1 * quantity;
        }

        @Override
        public List<Transaction> getTransactions(Double balance, PortfolioStock portfolioStock,
                                                 TradeRequest request, TransactionRepository repository,boolean shortSell) {

            TransactionContext context = createTransactionContext(balance, portfolioStock, request);

            if (!shortSell) {
                return handleNormalSell(context, repository);
            } else {
                return handleShortSell(context);
            }
        }
    };

    protected Double quantity;

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public abstract Double getQuantity();

    public abstract List<Transaction> getTransactions(Double balance,
                                                      PortfolioStock portfolioStock,
                                                      TradeRequest tradeRequest,
                                                      TransactionRepository transactionRepository,
                                                      boolean isShortSell);

    protected static class TransactionContext {
        final Double balance;
        final PortfolioStock portfolioStock;
        final TradeRequest request;
        final Double requestedQuantity;
        final AbstractConfigValidator configValidator;
        final Double marginRate;
        final TransactionRequest transactionRequest;

        TransactionContext(Double balance, PortfolioStock portfolioStock, TradeRequest request,
                           boolean isSell) {
            this.balance = balance;
            this.portfolioStock = portfolioStock;
            this.request = request;
            this.requestedQuantity = request.lotSize() * portfolioStock.getStock().getLotSize();
            this.configValidator = Exchange.getAbstractConfigValidator(isSell, portfolioStock.getStock());
            this.marginRate = configValidator.getMargin(portfolioStock.getPortfolio().getUserId(),
                    request.orderValidity());
            this.transactionRequest = createTransactionRequest(portfolioStock, request);
        }
    }

    protected TransactionContext createTransactionContext(Double balance, PortfolioStock portfolioStock,
                                                          TradeRequest request) {
        boolean isSell = this == SELL;
        return new TransactionContext(balance, portfolioStock, request, isSell);
    }

    protected static TransactionRequest createTransactionRequest(PortfolioStock portfolioStock,
                                                                 TradeRequest request) {
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setUserId(portfolioStock.getPortfolio().getUserId());
        transactionRequest.setStock(portfolioStock.getStock());
        transactionRequest.setAskedLotSize(request.lotSize());
        return transactionRequest;
    }

    protected List<Transaction> handleNormalBuy(TransactionContext ctx) {
        List<Transaction> transactions = new ArrayList<>();

        validateMarginAndTransaction(ctx);

        Transaction transaction = createTransaction(ctx.portfolioStock, ctx.request, ctx.requestedQuantity, this);
        transaction.setMargin(calculateMarginUsed(ctx.request, ctx.portfolioStock.getStock(), ctx.marginRate));

        transactions.add(transaction);
        return transactions;
    }

    protected List<Transaction> handleShortCover(TransactionContext ctx, TransactionRepository repository) {
        List<Transaction> transactions = new ArrayList<>();
        Double remainingQty = ctx.requestedQuantity;

        List<Transaction> sellTransactions = findOpenTransactions(ctx.portfolioStock, repository, SELL);

        for (Transaction sellTransaction : sellTransactions) {
            if (remainingQty <= 0) break;

            Transaction coverTransaction = createCoverTransaction(ctx, sellTransaction, remainingQty);
            remainingQty -= coverTransaction.getQty();
            transactions.add(coverTransaction);

            updateParentTransaction(sellTransaction, coverTransaction.getQty(), repository);
        }

        if (remainingQty > 0) {
            handleRemainingCover(transactions, ctx, remainingQty);
        }

        return transactions;
    }

    protected List<Transaction> handleNormalSell(TransactionContext ctx, TransactionRepository repository) {
        List<Transaction> transactions = new ArrayList<>();
        Double remainingQty = ctx.requestedQuantity;

        List<Transaction> buyTransactions = findOpenTransactions(ctx.portfolioStock, repository, BUY);

        for (Transaction buyTransaction : buyTransactions) {
            if (remainingQty <= 0) break;

            Transaction sellTransaction = createSellFromBuyTransaction(ctx, buyTransaction, remainingQty);
            remainingQty -= sellTransaction.getQty();
            transactions.add(sellTransaction);

            updateParentTransaction(buyTransaction, sellTransaction.getQty(), repository);
        }

        if (remainingQty > 0) {
            handleRemainingShortSell(transactions, ctx, remainingQty);
        }

        return transactions;
    }

    protected List<Transaction> handleShortSell(TransactionContext ctx) {
        List<Transaction> transactions = new ArrayList<>();

        validateMarginAndTransaction(ctx);

        Transaction transaction = createTransaction(ctx.portfolioStock, ctx.request, ctx.requestedQuantity, this);
        transaction.setMargin(calculateMarginUsed(ctx.request, ctx.portfolioStock.getStock(), ctx.marginRate));

        transactions.add(transaction);
        return transactions;
    }

    protected void validateMarginAndTransaction(TransactionContext ctx) {
        double marginUsed = calculateMarginUsed(ctx.request, ctx.transactionRequest.getStock(), ctx.marginRate);
        if (ctx.balance < marginUsed) {
            throw new TradeValidationException(TradeValidationErrorCode.INSUFFICIENT_MARGIN);
        }
        ctx.configValidator.validate(ctx.transactionRequest);
    }

    protected double calculateMarginUsed(TradeRequest request, Stock stock, Double marginRate) {
        Double askedQty = request.lotSize() * stock.getLotSize();
        return (askedQty * request.askedPrice()) / marginRate;
    }

    protected List<Transaction> findOpenTransactions(PortfolioStock portfolioStock,
                                                     TransactionRepository repository, OrderType type) {
        return repository.findAllByPortfolioStockAndQtyGreaterThanAndDeleteflagAndOrderType(
                portfolioStock, 0.0, 0, type);
    }

    protected Transaction createTransaction(PortfolioStock portfolioStock, TradeRequest request,
                                            Double quantity, OrderType orderType) {
        Transaction transaction = new Transaction();
        transaction.setQty(quantity);
        transaction.setTradedQty(quantity);
        transaction.setPrice(request.askedPrice());
        transaction.setOrderType(orderType);
        transaction.setRequestTimestamp(DateTimeUtil.getCurrentDateTime());
        transaction.setPortfolioStock(portfolioStock);
        return transaction;
    }

    protected Transaction createCoverTransaction(TransactionContext ctx, Transaction sellTransaction,
                                                 Double qtyToCover) {
        Double qty = Math.min(sellTransaction.getQty(), qtyToCover);

        Transaction transaction = createTransaction(ctx.portfolioStock, ctx.request, qty, BUY);
        transaction.setParentTransaction(sellTransaction);
        transaction.setDeleteflag(1);
        transaction.setProfitLoss(calculateProfitLoss(qty, ctx.request.askedPrice(),
                sellTransaction.getExecutedPrice()));

        return transaction;
    }

    protected Transaction createSellFromBuyTransaction(TransactionContext ctx, Transaction buyTransaction,
                                                       Double qtyToSell) {
        Double qty = Math.min(buyTransaction.getQty(), qtyToSell);

        Transaction transaction = createTransaction(ctx.portfolioStock, ctx.request, qty, SELL);
        transaction.setParentTransaction(buyTransaction);
        transaction.setDeleteflag(1);
        transaction.setProfitLoss(calculateProfitLoss(qty, ctx.request.askedPrice(),
                buyTransaction.getExecutedPrice()));

        return transaction;
    }

    protected void updateParentTransaction(Transaction parentTransaction, Double quantity,
                                           TransactionRepository repository) {
        parentTransaction.setQty(parentTransaction.getQty() - quantity);
        repository.save(parentTransaction);
    }

    protected Double calculateProfitLoss(Double quantity, Double currentPrice, Double executedPrice) {
        return (currentPrice * quantity) - (executedPrice * quantity);
    }

    protected void handleRemainingCover(List<Transaction> transactions, TransactionContext ctx,
                                        Double remainingQty) {
        validateMarginAndTransaction(ctx);

        Transaction transaction = createTransaction(ctx.portfolioStock, ctx.request, remainingQty, BUY);
        transaction.setMargin(calculateMarginUsed(ctx.request, ctx.portfolioStock.getStock(), ctx.marginRate));

        transactions.add(transaction);
    }

    protected void handleRemainingShortSell(List<Transaction> transactions, TransactionContext ctx,
                                            Double remainingQty) {
        validateMarginAndTransaction(ctx);

        Transaction transaction = createTransaction(ctx.portfolioStock, ctx.request, remainingQty, SELL);
        transaction.setMargin(calculateMarginUsed(ctx.request, ctx.portfolioStock.getStock(), ctx.marginRate));

        transactions.add(transaction);
    }
}