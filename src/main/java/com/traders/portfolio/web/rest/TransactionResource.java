package com.traders.portfolio.web.rest;

import com.traders.common.appconfig.util.PaginationUtil;
import com.traders.common.model.TradeOrderDetails;
import com.traders.portfolio.service.TransactionService;
import com.traders.portfolio.service.dto.*;
import com.traders.portfolio.trades.dto.ActiveTradesResponseDTO;
import com.traders.portfolio.trades.dto.ClosedTradesResponseDTO;
import com.traders.portfolio.trades.dto.PendingTradesResponseDTO;
import com.traders.portfolio.trades.service.TradesService;
import com.traders.portfolio.utils.UserIdSupplier;
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
    private final TradesService tradesService;

    public TransactionResource(TransactionService transactionService, TradesService tradesService) {

        this.transactionService = transactionService;
        this.tradesService = tradesService;
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
    public ResponseEntity<List<TradeOrderDetails>> addTradeTransaction(@RequestBody TradeRequest tradeRequest) {
        LOG.debug("REST request to add transaction");
        String userId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        return ResponseEntity.ok(transactionService.addTransaction(userId,tradeRequest));
    }

    @PostMapping("/transactions/update")
    @ResponseStatus(HttpStatus.CREATED)
    public void updateTransaction(@RequestBody TransactionUpdateRecord transactionUpdateDTO) {
        LOG.debug("REST request to update transaction");
        transactionService.updateTransactionStatus(transactionUpdateDTO);
    }

    @PutMapping("/transactions/update/cancel")
    @ResponseStatus(HttpStatus.OK)
    public void cancelTransaction(@RequestBody CancelTransactionRecord cancelTransactionRecord) {
        LOG.debug("REST request to cancel transaction");
        Long userId=UserIdSupplier.getUserId();
        transactionService.cancelTransaction(userId,cancelTransactionRecord);
    }

    @PutMapping("/transactions/update/pending-order")
    @ResponseStatus(HttpStatus.OK)
    public void updatePendingOrder(@RequestBody UpdatePendingTransactionRecord updatePendingTransactionRecord) {
        LOG.debug("REST request to Update Pending transaction");
        Long userId=UserIdSupplier.getUserId();
        transactionService.updatePendingTransaction(userId,updatePendingTransactionRecord);
    }

    @GetMapping("/transactions/closed/{userId}")
    public ResponseEntity<List<ClosedTradesResponseDTO>> getClosedTradesForUser(int size,int page,@PathVariable String userId,@RequestParam(required = false) Map<String, Object> filters) {
        LOG.debug("REST request to get closed trades for user by admin");

        if (filters == null) {
            filters = new HashMap<>();
        }
        filters.put("userId", userId);
        List<ClosedTradesResponseDTO> closedTrades=tradesService.getClosedTrades(size,page,filters);
        return new ResponseEntity<>(closedTrades,HttpStatus.OK);
    }

    @GetMapping("/transactions/closed")
    public ResponseEntity<List<ClosedTradesResponseDTO>> getClosedTrades(int size,int page,@RequestParam(required = false) Map<String, Object> filters) {
        LOG.debug("REST request to get closed trades");
        String userId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        if (filters == null) {
            filters = new HashMap<>();
        }
        filters.put("userId",userId);

        List<ClosedTradesResponseDTO> closedTrades=tradesService.getClosedTrades(size,page,filters);
        return new ResponseEntity<>(closedTrades,HttpStatus.OK);
    }

    @GetMapping("/transactions/pending/{userId}")
    public ResponseEntity<List<PendingTradesResponseDTO>> getPendingTradesForUser(int size,int page,@PathVariable String userId,@RequestParam(required = false) Map<String, Object> filters) {
        LOG.debug("REST request to get pending trades for user by admin");

        if (filters == null) {
            filters = new HashMap<>();
        }
        filters.put("userId", userId);
        List<PendingTradesResponseDTO> pendingTrades=tradesService.getPendingTrades(size,page,filters);
        return new ResponseEntity<>(pendingTrades,HttpStatus.OK);
    }

    @GetMapping("/transactions/pending")
    public ResponseEntity<List<PendingTradesResponseDTO>> getPendingTrades(int size, int page, @RequestParam(required = false) Map<String, Object> filters) {
        LOG.debug("REST request to get pending trades");
        String userId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        if (filters == null) {
            filters = new HashMap<>();
        }
        filters.put("userId",userId);

        List<PendingTradesResponseDTO> pendingTrades=tradesService.getPendingTrades(size,page,filters);
        return new ResponseEntity<>(pendingTrades,HttpStatus.OK);
    }


    @GetMapping("/transactions/active/{userId}")
    public ResponseEntity<List<ActiveTradesResponseDTO>> getActiveTradesForUser(int size,int page,@PathVariable String userId,@RequestParam(required = false) Map<String, Object> filters) {
        LOG.debug("REST request to get active trades for user by admin");

        if (filters == null) {
            filters = new HashMap<>();
        }
        filters.put("userId", userId);
        List<ActiveTradesResponseDTO> activeTrades=tradesService.getAllActiveTrades(size,page,filters);
        return new ResponseEntity<>(activeTrades,HttpStatus.OK);
    }

    @GetMapping("/transactions/active")
    public ResponseEntity<List<ActiveTradesResponseDTO>> getActiveTrades(int size, int page, @RequestParam(required = false) Map<String, Object> filters) {
        LOG.debug("REST request to get active trades");
        String userId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        if (filters == null) {
            filters = new HashMap<>();
        }
        filters.put("userId",userId);
        List<ActiveTradesResponseDTO> activeTrades=tradesService.getAllActiveTrades(size,page,filters);
        return new ResponseEntity<>(activeTrades,HttpStatus.OK);
    }
}
