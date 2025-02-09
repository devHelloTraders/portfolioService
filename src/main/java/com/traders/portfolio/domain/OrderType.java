package com.traders.portfolio.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.traders.common.utils.DateTimeUtil;
import com.traders.portfolio.repository.TransactionRepository;
import com.traders.portfolio.service.dto.TradeRequest;

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
        public List<Transaction> getTransactions(Double margin,PortfolioStock portfolioStock
                ,TradeRequest request,TransactionRepository repository) {
            List<Transaction> transactions = new ArrayList<>();
            if(portfolioStock.getQuantity()>=0){
                //Normal Buy
                Transaction transaction = new Transaction();
                transaction.setQty(request.lotSize()*portfolioStock.getStock().getLotSize());
                transaction.setTradedQty(request.lotSize()*portfolioStock.getStock().getLotSize());
                transaction.setPrice(request.askedPrice());
                transaction.setOrderType(this);
                transaction.setRequestTimestamp(DateTimeUtil.getCurrentDateTime());
                transaction.setMargin(margin);
                transaction.setPortfolioStock(portfolioStock);

                transactions.add(transaction);
            }else if(portfolioStock.getQuantity()<0){
                //SHORT COVER
                List<Transaction> sellTransactions=repository.findAllByPortfolioStockAndQtyGreaterThanAndDeleteflagAndOrderType(
                        portfolioStock,0.0,0,OrderType.SELL);
                Double qtyToCover=request.lotSize()*portfolioStock.getStock().getLotSize(); //TODO: Set request.lotSize here
                for(Transaction sellTransaction:sellTransactions){
                    Double qty=Math.min(sellTransaction.getQty(),qtyToCover);
                    Transaction transaction=new Transaction();
                    transaction.setQty(qty);
                    transaction.setTradedQty(qty);
                    transaction.setPrice(request.askedPrice());
                    transaction.setOrderType(this);
                    transaction.setRequestTimestamp(DateTimeUtil.getCurrentDateTime());
                    transaction.setParentTransaction(sellTransaction);
                    transaction.setPortfolioStock(portfolioStock);
                    transaction.setDeleteflag(1);
                    transactions.add(transaction);

                    sellTransaction.setQty(sellTransaction.getQty()-qty);
                    repository.save(sellTransaction);

                    qtyToCover-=qty;
                    if(qtyToCover<=0) break;
                }
                if(qtyToCover>0){
                    Transaction transaction=new Transaction();
                    transaction.setQty(qtyToCover);
                    transaction.setTradedQty(qtyToCover);
                    transaction.setPrice(request.askedPrice());
                    transaction.setOrderType(this);
                    transaction.setRequestTimestamp(DateTimeUtil.getCurrentDateTime());
                    transaction.setPortfolioStock(portfolioStock);
                    transactions.add(transaction);
                }
            }
            return transactions;
        }

    },SELL {
        @Override
        public Double getQuantity() {
            return -1* quantity;
        }

        @Override
        public List<Transaction> getTransactions(Double margin,PortfolioStock portfolioStock,TradeRequest request,TransactionRepository repository) {
            List<Transaction> transactions = new ArrayList<>();
            if(portfolioStock.getQuantity()>=0){
                //Normal Sell
                List<Transaction> buyTransactions=repository.findAllByPortfolioStockAndQtyGreaterThanAndDeleteflagAndOrderType(
                        portfolioStock,0.0,0,OrderType.BUY);
                Double qtyToSell=request.lotSize()*portfolioStock.getStock().getLotSize();
                for(Transaction buyTransaction:buyTransactions){
                    Double qty=Math.min(buyTransaction.getQty(),qtyToSell);
                    Transaction transaction=new Transaction();
                    transaction.setQty(qty);
                    transaction.setTradedQty(qty);
                    transaction.setPrice(request.askedPrice());
                    transaction.setOrderType(this);
                    transaction.setRequestTimestamp(DateTimeUtil.getCurrentDateTime());
                    transaction.setParentTransaction(buyTransaction);
                    transaction.setPortfolioStock(portfolioStock);
                    transaction.setDeleteflag(1);
                    transactions.add(transaction);

                    buyTransaction.setQty(buyTransaction.getQty()-qty);
                    repository.save(buyTransaction);

                    qtyToSell-=qty;
                    if(qtyToSell <=0) break;
                }
                if(qtyToSell>0){
                    Transaction transaction=new Transaction();
                    transaction.setQty(qtyToSell);
                    transaction.setTradedQty(qtyToSell);
                    transaction.setPrice(request.askedPrice());
                    transaction.setOrderType(this);
                    transaction.setRequestTimestamp(DateTimeUtil.getCurrentDateTime());
                    transaction.setPortfolioStock(portfolioStock);
                    transactions.add(transaction);
                }

            }else if(portfolioStock.getQuantity()<0){
                //Short sell
                Transaction transaction = new Transaction();
                transaction.setQty(request.lotSize()*portfolioStock.getStock().getLotSize());
                transaction.setTradedQty(request.lotSize()*portfolioStock.getStock().getLotSize());
                transaction.setPrice(request.askedPrice());
                transaction.setOrderType(this);
                transaction.setRequestTimestamp(DateTimeUtil.getCurrentDateTime());
                transaction.setMargin(margin);
                transaction.setPortfolioStock(portfolioStock);

                transactions.add(transaction);

            }
            return transactions;
        }
    };
    Double quantity;
    public void setQuantity(Double quantity){
        this.quantity = quantity;
    }
    public abstract Double getQuantity();
    public abstract List<Transaction> getTransactions(Double margin,
                                                      PortfolioStock portfolioStock,
                                                      TradeRequest tradeRequest,
                                                      TransactionRepository transactionRepository);
}