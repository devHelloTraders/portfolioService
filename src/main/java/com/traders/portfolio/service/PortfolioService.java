package com.traders.portfolio.service;

import com.traders.common.utils.CommonValidations;
import com.traders.portfolio.domain.Portfolio;
import com.traders.portfolio.domain.PortfolioStock;
import com.traders.portfolio.domain.Transaction;
import com.traders.portfolio.exception.BadRequestAlertException;
import com.traders.portfolio.repository.PortfolioRepository;
import com.traders.portfolio.service.dto.PortfolioDTO;
import com.traders.portfolio.service.dto.PortfolioStockDTO;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final RedisService redisService;
    private final ModelMapper modelMapper;

    public PortfolioService(PortfolioRepository portfolioRepository, RedisService redisService, ModelMapper modelMapper) {
        this.portfolioRepository = portfolioRepository;
        this.redisService = redisService;
        this.modelMapper = modelMapper;
    }
    @Transactional
    public PortfolioDTO getUserPortfolio(String userId){
        long id;
        if((id =CommonValidations.getNumber(userId,Long.class))==0)
            throw new BadRequestAlertException("Invalid User details", "PortfolioService", "Not valid user passed in request");

        return getPortfolio(id)
                           .map(this::getPortfolioDTO).orElseGet(PortfolioDTO::new);

    }

    private PortfolioDTO getPortfolioDTO(Portfolio portfolio){
        PortfolioDTO portfolioDTO = new PortfolioDTO();
        modelMapper.map(portfolio,portfolioDTO);
        portfolioDTO.getStocks()
                .forEach(stock->stock.setCurrentPrice(redisService.getDoubleValue(stock.getStock().getCurrentPriceKey())));
        var portfolioPrice =portfolioDTO.getStocks()
                .stream()
                .reduce(
                        Pair.of(0.0, 0.0), // Initial accumulator: (investment, currentValue, unused)
                        (acc, stock) -> Pair.of(
                                acc.getLeft() + stock.getQuantity() * stock.getCurrentPrice(), // Update investment
                                acc.getRight() + stock.getQuantity() * stock.getAverageCost()
                        ),
                        (acc1, acc2) -> Pair.of(
                                acc1.getLeft() + acc2.getLeft(),
                                acc1.getRight() + acc2.getRight()
                        )
                );

        portfolioDTO.calculatePortfolio(portfolioPrice);
        return portfolioDTO;
    }


    private Optional<Portfolio> getPortfolio(long userId){
        return portfolioRepository.findByUserId(userId);
    }

    private Portfolio savePortfolio(Portfolio portfolio){
        return portfolioRepository.save(portfolio);
    }
    public void addTransactionToPortfolio(Long userId, Transaction transaction){
        var portfolio = getPortfolio(userId).orElseGet(()->new Portfolio(userId));
        var stock = portfolio.getStocks().stream()
                .filter(Objects::nonNull)
                .filter(portfolioStock-> Objects.equals(portfolioStock.getStock().getId(), transaction.getStock().getId()))
                .findFirst()
                .orElseGet(()->new PortfolioStock(portfolio,transaction.getStock()));
        stock.getTransactions().add(transaction);
        stock.addQuantity(transaction.getOrderType().getQuantity(),transaction.getPrice());
        portfolio.getStocks().add(stock);
        savePortfolio(portfolio);
    }

}
