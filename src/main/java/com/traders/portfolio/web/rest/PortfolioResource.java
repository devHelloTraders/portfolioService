package com.traders.portfolio.web.rest;

import com.traders.common.appconfig.util.HeaderUtil;
import com.traders.common.appconfig.util.PaginationUtil;
import com.traders.portfolio.service.PortfolioService;
import com.traders.portfolio.service.dto.PortfolioDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api")
public class PortfolioResource {


    private static final Logger LOG = LoggerFactory.getLogger(PortfolioResource.class);
    private final PortfolioService portfolioService;
    @Value("${config.clientApp.name}")
    private String applicationName;

    public PortfolioResource(PortfolioService portfolioService) {

        this.portfolioService = portfolioService;
    }


    @GetMapping("/portfolio")
    public ResponseEntity<PortfolioDTO> getPortfolio() {
        LOG.debug("REST request to get portfolio");
        String userId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        return new ResponseEntity<>(portfolioService.getUserPortfolio(userId),
                HeaderUtil.createAlert(applicationName, "portfolio retrived for user: "+userId,userId),
                HttpStatus.OK);
    }


}
