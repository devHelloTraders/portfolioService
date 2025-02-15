package com.traders.portfolio.trades.service;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class TradesQueryBuilder {

    /**
     * @return This method will return query for all closed trades with
     * userId where condition.
     */
    public String queryForClosedTrades(int size,int page,Map<String,Object> filters){
        StringBuilder query= new StringBuilder();
        query.append("SELECT trades.id,trades.scriptName,trades.exchange,trades.userId,trades.userName,trades.buyPrice,trades.sellPrice,trades.qty,trades.lotSize,trades.completedTrade,trades.shortSellTrade,trades.sellAtTime,trades.buyAtTime,trades.timeDiffInSeconds,trades.instrumentToken FROM ")
                .append("(")
                .append(queryForCompleteBuyTransactions())
                .append(" UNION ")
                .append(queryForCompleteSellTransactions())
                .append(") AS trades");
        if(filters != null && !filters.isEmpty()){
            query.append(" WHERE 1=1");
            if(filters.containsKey("startDate") && filters.containsKey("endDate")){
                query.append(" AND trades.completedTrade BETWEEN :startDate AND :endDate");
            }
        }
        query.append(" LIMIT ").append(size);
        if(page>0){
            query.append(" OFFSET ").append(page*size);
        }
        return query.toString();
    }


    /**
     * @return This method will return query for all active trades with
     * userId where condition.
     */
    public String queryForActiveTrades(int size,int page){
        StringBuilder query= new StringBuilder();
        query.append("SELECT trades.id,trades.scriptName,trades.exchange,trades.orderType,trades.userId,trades.userName,trades.buyPrice,trades.sellPrice,trades.qty,trades.lotSize,trades.completedTrade,trades.shortSellTrade,trades.sellAtTime,trades.buyAtTime,trades.instrumentToken,trades.exchangeSegment,trades.tradingSymbol FROM ")
                .append("(")
                .append(queryForActiveBuys())
                .append(" UNION ")
                .append(queryForActiveSells())
                .append(") AS trades")
                .append(" LIMIT ").append(size);
        if(page>0){
            query.append(" OFFSET ").append(page*size);
        }
        return query.toString();
    }


    public String queryForPendingTrades(int size,int page,Map<String,Object> filters){
        StringBuilder query= new StringBuilder();
        query.append("SELECT t.id,t.order_type AS orderType,t.qty,t.qty/s.lot_size AS lotSize,s.name AS scriptName,t.price AS rate,t.request_timestamp AS requestedTime,s.instrument_token AS instrumentToken,")
                .append("s.exchange ")
                .append("FROM ")
                .append("transaction t ")
                .append("JOIN portfolio_stocks_detail psd ON t.portfoliostock_id=psd.id ")
                .append("JOIN portfolio p ON psd.portfolio_id = p.id ")
                .append("JOIN user u ON u.id=p.user_id ")
                .append("JOIN stock_details s ON s.id=psd.stock_id ")
                .append("WHERE ")
                .append("u.id = :userId AND t.transaction_status IN ('PENDING','CANCELLED')");
        if(filters != null && !filters.isEmpty()){
            if(filters.containsKey("exchange")){
                query.append(" AND s.exchange = :exchange");
            }
        }
        query.append(" LIMIT ").append(size);
        if(page>0){
            query.append(" OFFSET ").append(page*size);
        }
        return query.toString();
    }

    /**
     * The Following method will create a query for a scenario where
     * client buys first and then sell those scripts.
     * (BUY - SELL)
     * @return
     */
    private String queryForCompleteBuyTransactions(){
        StringBuilder query = new StringBuilder("SELECT ")
                .append("t_sell.id AS id,")
                .append("s.name AS scriptName,")
                .append("s.exchange AS exchange,")
                .append("s.instrument_token AS instrumentToken,")
                .append("u.id AS userId,")
                .append("CONCAT(u.first_name,' ',u.last_name) AS userName,")
                .append("t_buy.executed_price AS buyPrice,")
                .append("t_sell.executed_price AS sellPrice,")
                .append("t_sell.qty AS qty,")
                .append("t_sell.qty/s.lot_size AS lotSize,")
                .append("1 AS completedTrade,")
                .append("0 AS shortSellTrade,")
                .append("t_buy.completed_timestamp AS buyAtTime,")
                .append("t_sell.completed_timestamp AS sellAtTime,")
                .append("TIMESTAMPDIFF(SECOND,t_buy.completed_timestamp,t_sell.completed_timestamp) AS timeDiffInSeconds ")
                .append("FROM ")
                .append("transaction t_buy ")
                .append("LEFT JOIN transaction t_sell ON t_buy.id=t_sell.parent_transaction_id ")
                .append("JOIN portfolio_stocks_detail psd ON t_buy.portfoliostock_id=psd.id ")
                .append("JOIN portfolio p ON psd.portfolio_id = p.id ")
                .append("JOIN user u ON u.id=p.user_id ")
                .append("JOIN stock_details s ON s.id=psd.stock_id ")
                .append("WHERE ")
                .append("t_buy.order_type='BUY' AND t_sell.order_type='SELL' ")
                .append("AND u.id = :userId");
        return query.toString();
    }

    /**
     * The follwing method will create a query for a scenario where
     * client sells first and then buy those scripts.
     * (Short Selling - Short cover)
     * @return
     */
    private String queryForCompleteSellTransactions(){
        StringBuilder query = new StringBuilder("SELECT ")
                .append("t_buy.id AS id,")
                .append("s.name AS scriptName,")
                .append("s.exchange AS exchange,")
                .append("s.instrument_token AS instrumentToken,")
                .append("u.id AS userId,")
                .append("CONCAT(u.first_name,' ',u.last_name) AS userName,")
                .append("t_buy.executed_price AS buyPrice,")
                .append("t_sell.executed_price AS sellPrice,")
                .append("t_buy.qty AS qty,")
                .append("t_buy.qty/s.lot_size AS lotSize,")
                .append("1 AS completedTrade,")
                .append("1 AS shortSellTrade,")
                .append("t_buy.completed_timestamp AS buyAtTime,")
                .append("t_sell.completed_timestamp AS sellAtTime,")
                .append("TIMESTAMPDIFF(SECOND,t_sell.completed_timestamp,t_buy.completed_timestamp) AS timeDiffInSeconds ")
                .append("FROM ")
                .append("transaction t_sell ")
                .append("LEFT JOIN transaction t_buy ON t_sell.id=t_buy.parent_transaction_id ")
                .append("JOIN portfolio_stocks_detail psd ON t_buy.portfoliostock_id=psd.id ")
                .append("JOIN portfolio p ON psd.portfolio_id = p.id ")
                .append("JOIN user u ON u.id=p.user_id ")
                .append("JOIN stock_details s ON s.id=psd.stock_id ")
                .append("WHERE ")
                .append("t_buy.order_type='BUY' AND t_sell.order_type='SELL' ")
                .append("AND u.id = :userId");
        return query.toString();
    }

    private String queryForActiveBuys(){
        StringBuilder query = new StringBuilder("SELECT ")
                .append("t_buy.id AS id,")
                .append("s.name AS scriptName,")
                .append("s.exchange AS exchange,")
                .append("s.exchange_segment AS exchangeSegment,")
                .append("s.symbol AS tradingSymbol,")
                .append("s.instrument_token AS instrumentToken,")
                .append("t_buy.order_type AS orderType,")
                .append("u.id AS userId,")
                .append("CONCAT(u.first_name,' ',u.last_name) AS userName,")
                .append("t_buy.executed_price AS buyPrice,")
                .append("NULL AS sellPrice,")
                .append("t_buy.qty AS qty,")
                .append("t_buy.qty/s.lot_size AS lotSize,")
                .append("0 AS completedTrade,")
                .append("0 AS shortSellTrade,")
                .append("t_buy.completed_timestamp AS buyAtTime,")
                .append("NULL AS sellAtTime,")
                .append("NULL AS timeDiffInSeconds ")
                .append("FROM ")
                .append("transaction t_buy ")
                .append("JOIN portfolio_stocks_detail psd ON t_buy.portfoliostock_id=psd.id ")
                .append("JOIN portfolio p ON psd.portfolio_id = p.id ")
                .append("JOIN user u ON u.id=p.user_id ")
                .append("JOIN stock_details s ON s.id=psd.stock_id ")
                .append("WHERE ")
                .append("t_buy.order_type='BUY' AND t_buy.qty>0 AND t_buy.parent_transaction_id IS NULL AND ")
                .append("u.id = :userId");
        return query.toString();
    }

    private String queryForActiveSells(){
        StringBuilder query = new StringBuilder("SELECT ")
                .append("t_sell.id AS id,")
                .append("s.name AS scriptName,")
                .append("s.exchange AS exchange,")
                .append("s.exchange_segment AS exchangeSegment,")
                .append("s.symbol AS tradingSymbol,")
                .append("s.instrument_token AS instrumentToken,")
                .append("t_sell.order_type AS orderType,")
                .append("u.id AS userId,")
                .append("CONCAT(u.first_name,' ',u.last_name) AS userName,")
                .append("NULL AS buyPrice,")
                .append("t_sell.executed_price AS sellPrice,")
                .append("t_sell.qty AS qty,")
                .append("t_sell.qty/s.lot_size AS lotSize,")
                .append("0 AS completedTrade,")
                .append("1 AS shortSellTrade,")
                .append("NULL AS buyAtTime,")
                .append("t_sell.completed_timestamp AS sellAtTime,")
                .append("NULL AS timeDiffInSeconds ")
                .append("FROM ")
                .append("transaction t_sell ")
                .append("JOIN portfolio_stocks_detail psd ON t_sell.portfoliostock_id=psd.id ")
                .append("JOIN portfolio p ON psd.portfolio_id = p.id ")
                .append("JOIN user u ON u.id=p.user_id ")
                .append("JOIN stock_details s ON s.id=psd.stock_id ")
                .append("WHERE ")
                .append("t_sell.order_type='SELL' AND t_sell.qty>0 AND t_sell.parent_transaction_id IS NULL AND ")
                .append("u.id = :userId");
        return query.toString();
    }
}
