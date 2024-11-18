package com.traders.portfolio.web.rest;

import com.traders.common.appconfig.util.HeaderUtil;
import com.traders.common.constants.AuthoritiesConstants;
import com.traders.portfolio.service.TransactionService;
import com.traders.portfolio.service.dto.TransactionDTO;
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
public class TransactionResource {


    private static final Logger LOG = LoggerFactory.getLogger(TransactionResource.class);
    private final TransactionService transactionService;
    @Value("${config.clientApp.name}")
    private String applicationName;

    public TransactionResource(TransactionService transactionService) {

        this.transactionService = transactionService;
    }


    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionDTO>> getTransactions() {
        LOG.debug("REST request to get transaction");
        String userId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        return new ResponseEntity<>(transactionService.getTransactions(userId),
                HeaderUtil.createAlert(applicationName, "Transactions retrieved for user: "+userId,userId),
                HttpStatus.OK);
    }

    @PostMapping("/transactions/add")
    @ResponseStatus(HttpStatus.CREATED)
    public void getTransactions(@RequestBody TransactionDTO transactionDTO) {
        LOG.debug("REST request to add transaction");
        String userId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        transactionService.addTransaction(userId,transactionDTO);
    }

    @GetMapping("/transactionsForUser")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<List<TransactionDTO>> getTransactionsForUser(String userId) {
        LOG.debug("REST request to get transaction for user by admin");
        return new ResponseEntity<>(transactionService.getTransactions(userId),
                HeaderUtil.createAlert(applicationName, "Transactions retrieved for user: "+userId,userId),
                HttpStatus.OK);
    }


}
