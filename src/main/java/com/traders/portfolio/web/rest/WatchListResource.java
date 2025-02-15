package com.traders.portfolio.web.rest;

import com.traders.common.appconfig.util.PaginationUtil;
import com.traders.common.model.ExchangeSegment;
import com.traders.common.utils.CommonValidations;
import com.traders.portfolio.exception.BadRequestAlertException;
import com.traders.portfolio.service.WatchListService;
import com.traders.portfolio.service.WatchListStockService;
import com.traders.portfolio.service.dto.WatchListDTO;
import com.traders.portfolio.service.dto.WatchListStockDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api")
public class WatchListResource {
    private static final Logger LOG = LoggerFactory.getLogger(WatchListResource.class);
    private final WatchListService watchListService;
    private final WatchListStockService watchListStockService;
    @Value("${config.clientApp.name}")
    private String applicationName;

    private static final List<String> ALLOWED_ORDERED_PROPERTIES = List.of("id", "userId", "orderNum","stock.id", "stock.tradingSymbol","stock.instrumentToken",
            "stock.exchange","stock.segment","stock.expiry","stock.lastPrice");

    public WatchListResource(WatchListService watchListService, WatchListStockService watchListStockService) {

        this.watchListService = watchListService;
        this.watchListStockService = watchListStockService;
    }


//    @GetMapping("/watchlist")
//    public ResponseEntity<WatchListDTO> getWatchList() {
//        LOG.debug("REST request to get watchlist");
//        String userId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
//        return new ResponseEntity<>(watchListService.getWatchList(userId),
//                HeaderUtil.createAlert(applicationName, "watchlist retrived for user: "+userId,userId),
//                HttpStatus.OK);
//
//    }

    @GetMapping("/admin/watchlistForUser")
    public ResponseEntity<WatchListDTO> getWatchListForUser(String userId,Pageable pageable,@RequestParam(required = false)  Map<String,Object> filters) {
        LOG.debug("REST request to get watchlist for user from admin");

        WatchListDTO watchListDTO = watchListService.getWatchList(userId);
        final Page<WatchListStockDTO> page = watchListStockService.getFilterWatchListStocks(filters,pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        watchListDTO.setWatchListStocks(page.getContent());
        return new ResponseEntity<>(watchListDTO, headers, HttpStatus.OK);

    }


    @DeleteMapping("/watchlist/remove")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public  ResponseEntity<WatchListDTO>  removeFromWatchList(@RequestBody List<Long> stocksToDelete,Pageable pageable,@RequestParam(required = false)  Map<String,Object> filters) {
        LOG.debug("REST request to remove from watchlist");
        String userId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        long id;
        if((id = CommonValidations.getNumber(userId,Long.class))==0)
            throw new BadRequestAlertException("Invalid User details", "Watchlist service", "Not valid user passed in request");
        watchListService.removeFromWatchList(userId,stocksToDelete);
        return getWatchList(pageable,filters);
    }

    @PostMapping("/watchlist/add")
    public ResponseEntity<WatchListDTO> addStockInWatchlist(@RequestBody List<Long> stocksToAdd,Pageable pageable,@RequestParam(required = false)  Map<String,Object> filters) {
        LOG.debug("REST request to Add stock in watchlist");
        String userId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        long id;
        if((id = CommonValidations.getNumber(userId,Long.class))==0)
            throw new BadRequestAlertException("Invalid User details", "Watchlist service", "Not valid user passed in request");
        watchListService.addStockInWatchList(userId,stocksToAdd);
        return getWatchList(pageable,filters);
    }

    @PostMapping("/watchlist/update")
    public ResponseEntity<WatchListDTO> updateWatchList(@RequestBody List<Long> updatedStockIdList,Pageable pageable,@RequestParam(required = false)  Map<String,Object> filters) {
        LOG.debug("REST request to update stock in watchlist");
        String userId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        long id;
        if((id = CommonValidations.getNumber(userId,Long.class))==0)
            throw new BadRequestAlertException("Invalid User details", "Watchlist service", "Not valid user passed in request");
        watchListService.updateWatchlist(userId,updatedStockIdList);
        return getWatchList(pageable,filters);
    }



    @GetMapping("/watchlist")
    public ResponseEntity<WatchListDTO> getWatchList(Pageable pageable,@RequestParam(required = false)  Map<String,Object> filters) {
        LOG.debug("REST request to get watchlist");
        String userId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();

        if (!PaginationUtil.onlyContainsAllowedProperties(pageable,ALLOWED_ORDERED_PROPERTIES)) {
            return ResponseEntity.badRequest().build();
        }
        if (filters == null) {
            filters = new HashMap<>();
        }
        Optional<ExchangeSegment> exchangeSegment=watchListService.getExchangeSegment(filters);
        if(exchangeSegment.isEmpty())
            throw new BadRequestAlertException("Invalid Exchange Segment","WatchList service","Not a valid exchange segment");

        WatchListDTO watchListDTO = watchListService.getWatchList(userId);
        filters.put("watchList.id",watchListDTO.getId());
        final Page<WatchListStockDTO> page = watchListStockService.getFilterWatchListStocks(filters,pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        watchListDTO.setWatchListStocks(updateDTOWithMarketQuotes(page.getContent(),exchangeSegment.get()));
        return new ResponseEntity<>(watchListDTO, headers, HttpStatus.OK);
    }

    private List<WatchListStockDTO> updateDTOWithMarketQuotes(List<WatchListStockDTO> watchListStockDTOS,ExchangeSegment exchangeSegment){
        List<WatchListStockDTO> filteredScripts = watchListStockDTOS.stream().filter(watchListStockDTO ->
                exchangeSegment.getInstrumentTypes().contains(watchListStockDTO.getStock().getInstrumentType())
                && exchangeSegment.getExchanges().contains(watchListStockDTO.getStock().getExchange())).collect(Collectors.toList());
        return watchListService.mapQuotesToDTO(filteredScripts);
    }
}
