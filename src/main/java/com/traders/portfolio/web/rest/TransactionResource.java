package com.traders.portfolio.web.rest;

import com.traders.common.appconfig.util.PaginationUtil;
import com.traders.portfolio.service.TransactionService;
import com.traders.portfolio.service.dto.TradeRequest;
import com.traders.portfolio.service.dto.TransactionDTO;
import com.traders.portfolio.service.dto.TransactionUpdateRecord;
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

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api")
public class TransactionResource {

    private static final List<String> ALLOWED_ORDERED_PROPERTIES = List.of("id", "price", "requestTimestamp", "completedTimestamp","orderCategory","transactionStatus","orderType");

    private static final Logger LOG = LoggerFactory.getLogger(TransactionResource.class);
    private final TransactionService transactionService;
    @Value("${config.clientApp.name}")
    private String applicationName;

    public TransactionResource(TransactionService transactionService) {

        this.transactionService = transactionService;
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionDTO>> getTransactions( Pageable pageable,@RequestParam(required = false) Map<String, Object> filters) {
        LOG.debug("REST request to get transaction");
        String userId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        if (filters == null) {
            filters = new HashMap<>();
        }

        if (!PaginationUtil.onlyContainsAllowedProperties(pageable,ALLOWED_ORDERED_PROPERTIES)) {
            return ResponseEntity.badRequest().build();
        }

        final Page<TransactionDTO> page = transactionService.getFilteredTransactions(userId,filters,pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @PostMapping("/transactions/add")
    @ResponseStatus(HttpStatus.CREATED)
    public long addTradeTransaction(@RequestBody TradeRequest tradeRequest) {
        LOG.debug("REST request to add transaction");
        String userId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        return transactionService.addTransaction(userId,tradeRequest);
    }

    @PostMapping("/transactions/update")
    @ResponseStatus(HttpStatus.CREATED)
    public void updateTransaction(@RequestBody TransactionUpdateRecord transactionUpdateDTO) {
        LOG.debug("REST request to update transaction");
        transactionService.updateTransactionStatus(transactionUpdateDTO);
    }

    @GetMapping("/admin/transactionsForUser")
    public ResponseEntity<List<TransactionDTO>> getTransactionsForUser(String userId,Pageable pageable,@RequestParam(required = false) Map<String, Object> filters) {
        LOG.debug("REST request to get transaction for user by admin");


        if (!PaginationUtil.onlyContainsAllowedProperties(pageable,ALLOWED_ORDERED_PROPERTIES)) {
            return ResponseEntity.badRequest().build();
        }
        if (filters == null) {
            filters = new HashMap<>();
        }

        final Page<TransactionDTO> page = transactionService.getFilteredTransactions(userId,filters,pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
}
