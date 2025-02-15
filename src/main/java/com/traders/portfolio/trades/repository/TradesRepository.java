package com.traders.portfolio.trades.repository;

import com.traders.common.model.DepthDetails;
import com.traders.common.model.MarketDetailsRequest;
import com.traders.common.model.MarketQuotes;
import com.traders.portfolio.trades.dto.ActiveTradesResponseDTO;
import com.traders.portfolio.trades.dto.ClosedTradesResponseDTO;
import com.traders.portfolio.trades.dto.PendingTradesResponseDTO;
import com.traders.portfolio.trades.service.TradesQueryBuilder;
import com.traders.portfolio.web.rest.fign.DhanFeignService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class TradesRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final TradesQueryBuilder queryBuilder;
    private final DhanFeignService dhanService;
    private final Logger logger = LoggerFactory.getLogger(TradesRepository.class);

    public TradesRepository(NamedParameterJdbcTemplate jdbcTemplate, TradesQueryBuilder queryBuilder, DhanFeignService dhanService) {
        this.jdbcTemplate = jdbcTemplate;
        this.queryBuilder = queryBuilder;
        this.dhanService = dhanService;
    }


    public List<ClosedTradesResponseDTO> getClosedTrades(int size, int page, Map<String, Object> filters) {
        String query = queryBuilder.queryForClosedTrades(size, page, filters);
        try{
            return jdbcTemplate.query(query, filters, (rs, rowNum) -> {
                ClosedTradesResponseDTO dto = new ClosedTradesResponseDTO();
                Double qty=rs.getDouble("qty");
                Double buyPrice=rs.getDouble("buyPrice");
                Double sellPrice=rs.getDouble("sellPrice");
                boolean isShortSellTrade=rs.getBoolean("shortSellTrade");

                dto.setId(rs.getLong("id"));
                dto.setScriptName(rs.getString("scriptName"));
                dto.setExchange(rs.getString("exchange"));
                dto.setUserId(rs.getLong("userId"));
                dto.setUserName(rs.getString("userName"));
                dto.setBuyPrice(buyPrice);
                dto.setSellPrice(sellPrice);
                dto.setQty(qty);
                dto.setLotSize(rs.getDouble("lotSize"));
                dto.setSellAtTime(rs.getString("sellAtTime"));
                dto.setBuyAtTime(rs.getString("buyAtTime"));
                dto.setTimeDiffInSeconds(rs.getInt("timeDiffInSeconds"));
                if(isShortSellTrade){
                    dto.setProfitLoss(
                            (buyPrice*qty)-(sellPrice*qty)
                    );
                }else{
                    dto.setProfitLoss(
                            (sellPrice*qty)-(buyPrice*qty)
                    );
                }
                return dto;
            });
        }catch (Exception e){
            logger.debug(e.getMessage());
            return Collections.EMPTY_LIST;
        }
    }

    public List<PendingTradesResponseDTO> getPendingTrades(int size, int page, Map<String, Object> filters){
        String query=queryBuilder.queryForPendingTrades(size, page, filters);
        try{
            return jdbcTemplate.query(query,filters,new BeanPropertyRowMapper<>(PendingTradesResponseDTO.class));
        }catch (Exception e){
            logger.debug(e.getMessage());
            return Collections.EMPTY_LIST;
        }
    }

    public List<ActiveTradesResponseDTO> getActiveTrades(int size, int page, Map<String, Object> filters){
        String query=queryBuilder.queryForActiveTrades(size,page);
        try{
            MarketDetailsRequest marketDetailsRequest=new MarketDetailsRequest();
            List<ActiveTradesResponseDTO> activeTrades=jdbcTemplate.query(query,filters,new BeanPropertyRowMapper<>(ActiveTradesResponseDTO.class));
            activeTrades.forEach(trade->{
                marketDetailsRequest.addInstrument(MarketDetailsRequest.InstrumentDetails.of(
                        trade.getInstrumentToken(),
                        trade.getExchangeSegment(),
                        trade.getTradingSymbol()
                ));
            });
            var marketResponse=dhanService.getQuotesFromMarketList(marketDetailsRequest);
            return activeTrades.stream().map(trade->{
                MarketQuotes quotes=marketResponse.get(String.valueOf(trade.getInstrumentToken()));
                if(trade.getOrderType().equalsIgnoreCase("BUY")){
                    List<DepthDetails.Order> order= quotes.getDepthDetails().getSell();
                    if(order!=null && !order.isEmpty()){
                        trade.setProfitLoss(
                                trade.getQty()*order.getFirst().getPrice() - trade.getQty()*trade.getBuyPrice()
                        );
                    }
                }else{
                    List<DepthDetails.Order> order= quotes.getDepthDetails().getBuy();
                    if(order!=null && !order.isEmpty()){
                        trade.setProfitLoss(
                                trade.getQty()*order.getFirst().getPrice() - trade.getQty()*trade.getSellPrice()
                        );
                    }
                }
                return trade;
            }).collect(Collectors.toList());
        }catch (Exception e){
            logger.debug(e.getMessage());
            return Collections.EMPTY_LIST;
        }
    }

}
