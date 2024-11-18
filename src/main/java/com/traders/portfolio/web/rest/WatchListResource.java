package com.traders.portfolio.web.rest;

import com.traders.common.appconfig.util.HeaderUtil;
import com.traders.common.constants.AuthoritiesConstants;
import com.traders.common.utils.CommonValidations;
import com.traders.portfolio.exception.BadRequestAlertException;
import com.traders.portfolio.service.WatchListService;
import com.traders.portfolio.service.dto.WatchListDTO;
import com.traders.portfolio.service.dto.WatchListStockDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api")
public class WatchListResource {
    private static final Logger LOG = LoggerFactory.getLogger(WatchListResource.class);
    private final WatchListService watchListService;
    @Value("${config.clientApp.name}")
    private String applicationName;

    public WatchListResource(WatchListService watchListService) {

        this.watchListService = watchListService;
    }


    @GetMapping("/watchlist")
    public ResponseEntity<WatchListDTO> getWatchList() {
        LOG.debug("REST request to get watchlist");
        String userId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        return new ResponseEntity<>(watchListService.getWatchList(userId),
                HeaderUtil.createAlert(applicationName, "watchlist retrived for user: "+userId,userId),
                HttpStatus.OK);
    }

    @GetMapping("/watchlistForUser")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<WatchListDTO> getWatchListForUser(String userId) {
        LOG.debug("REST request to get watchlist for user from admin");
        return new ResponseEntity<>(watchListService.getWatchList(userId),
                HeaderUtil.createAlert(applicationName, "watchlist retrived for user: "+userId,userId),
                HttpStatus.OK);
    }


    @DeleteMapping("/watchlist/remove")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFromWatchList(@RequestBody List<Long> stocksToDelete) {
        LOG.debug("REST request to remove from watchlist");
        String userId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        long id;
        if((id = CommonValidations.getNumber(userId,Long.class))==0)
            throw new BadRequestAlertException("Invalid User details", "Watchlist service", "Not valid user passed in request");
        watchListService.removeFromWatchList(userId,stocksToDelete);
    }

    @PostMapping("/watchlist/add")
    public ResponseEntity<WatchListDTO> addStockInWatchlist(@RequestBody List<Long> stocksToAdd) {
        LOG.debug("REST request to Add stock in watchlist");
        String userId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        long id;
        if((id = CommonValidations.getNumber(userId,Long.class))==0)
            throw new BadRequestAlertException("Invalid User details", "Watchlist service", "Not valid user passed in request");
        ;
        return new ResponseEntity<>(watchListService.addStockInWatchList(userId,stocksToAdd),
                HeaderUtil.createAlert(applicationName, "watchlist updated for user: "+userId,userId),
                HttpStatus.OK);
    }

    @PostMapping("/watchlist/update")
    public ResponseEntity<WatchListDTO> updateWatchList(@RequestBody List<Long> updatedStockIdList) {
        LOG.debug("REST request to update stock in watchlist");
        String userId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        long id;
        if((id = CommonValidations.getNumber(userId,Long.class))==0)
            throw new BadRequestAlertException("Invalid User details", "Watchlist service", "Not valid user passed in request");
        ;
        return new ResponseEntity<>(watchListService.updateWatchlist(userId,updatedStockIdList),
                HeaderUtil.createAlert(applicationName, "watchlist updated for user: "+userId,userId),
                HttpStatus.OK);
    }




}
