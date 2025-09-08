package org.bukola.stockmarket.service;

import lombok.RequiredArgsConstructor;
import org.bukola.stockmarket.enums.TransactionType;
import org.bukola.stockmarket.model.*;
import org.bukola.stockmarket.repository.*;
import org.bukola.stockmarket.service.interfaces.ITradingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TradingService implements ITradingService {
    private final UserRepository userRepository;
    private final StockRepository stockRepository;
    private final PortfolioRepository portfolioRepository;
    private final TransactionRepository transactionRepository;

    @Override
    @Transactional
    public void buyStock(Long userId, String symbol, BigDecimal quantity, BigDecimal pricePerShare) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Stock stock = stockRepository.findBySymbol(symbol)
                .orElseThrow(() -> new RuntimeException("Stock not found"));

        Portfolio portfolio = portfolioRepository.findByUserAndStock(user, stock)
                .orElse(Portfolio.builder()
                        .user(user)
                        .stock(stock)
                        .quantityOwned(BigDecimal.ZERO)
                        .averagePurchasePrice(BigDecimal.ZERO)
                        .build());

        // calculate new average price
        BigDecimal totalShares = portfolio.getQuantityOwned().add(quantity);
        BigDecimal totalCost = portfolio.getAveragePurchasePrice().multiply(portfolio.getQuantityOwned())
                .add(pricePerShare.multiply(quantity));
        BigDecimal newAveragePrice = totalCost.divide(totalShares, BigDecimal.ROUND_HALF_UP);

        portfolio.setQuantityOwned(totalShares);
        portfolio.setAveragePurchasePrice(newAveragePrice);
        portfolioRepository.save(portfolio);

        Transaction transaction = Transaction.builder()
                .user(user)
                .stock(stock)
                .transactionType(TransactionType.BUY)
                .quantity(quantity)
                .pricePerShare(pricePerShare)
                .transactionTimestamp(LocalDateTime.now())
                .build();
        transactionRepository.save(transaction);
    }

    @Override
    @Transactional
    public void sellStock(Long userId, String symbol, BigDecimal quantity, BigDecimal pricePerShare) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Stock stock = stockRepository.findBySymbol(symbol)
                .orElseThrow(() -> new RuntimeException("Stock not found"));

        Portfolio portfolio = portfolioRepository.findByUserAndStock(user, stock)
                .orElseThrow(() -> new RuntimeException("Stock not in portfolio"));

        if (portfolio.getQuantityOwned().compareTo(quantity) < 0) {
            throw new RuntimeException("Not enough shares to sell");
        }

        BigDecimal newQuantity = portfolio.getQuantityOwned().subtract(quantity);
        portfolio.setQuantityOwned(newQuantity);

        if (newQuantity.compareTo(BigDecimal.ZERO) == 0) {
            portfolioRepository.delete(portfolio);
        } else {
            portfolioRepository.save(portfolio);
        }

        Transaction transaction = Transaction.builder()
                .user(user)
                .stock(stock)
                .transactionType(TransactionType.SELL)
                .quantity(quantity)
                .pricePerShare(pricePerShare)
                .transactionTimestamp(LocalDateTime.now())
                .build();
        transactionRepository.save(transaction);
    }
}
