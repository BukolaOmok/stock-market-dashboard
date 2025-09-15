package org.bukola.stockmarket.service;

import org.bukola.stockmarket.exceptions.InsufficientSharesException;
import org.bukola.stockmarket.exceptions.InvalidQuantityException;
import org.bukola.stockmarket.exceptions.StockNotFoundException;
import org.bukola.stockmarket.model.Portfolio;
import org.bukola.stockmarket.model.Stock;
import org.bukola.stockmarket.model.User;
import org.bukola.stockmarket.repository.PortfolioRepository;
import org.bukola.stockmarket.repository.StockRepository;
import org.bukola.stockmarket.repository.TransactionRepository;
import org.bukola.stockmarket.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TradingServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private PortfolioRepository portfolioRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TradingService tradingService;

    private User user;
    private Stock stock;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("Bukola");

        stock = new Stock();
        stock.setId(1L);
        stock.setSymbol("AAPL");
        stock.setCurrentPrice(BigDecimal.valueOf(150));
    }

    @Test
    void buyStock_shouldAddNewPortfolio_whenStockNotOwned() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(stockRepository.findBySymbol("AAPL")).thenReturn(Optional.of(stock));
        when(portfolioRepository.findByUserAndStock(user, stock)).thenReturn(Optional.empty());
        when(portfolioRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(transactionRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        tradingService.buyStock(1L, "AAPL", BigDecimal.valueOf(10), BigDecimal.valueOf(150));

        ArgumentCaptor<Portfolio> portfolioCaptor = ArgumentCaptor.forClass(Portfolio.class);
        verify(portfolioRepository).save(portfolioCaptor.capture());

        Portfolio savedPortfolio = portfolioCaptor.getValue();
        assertEquals(BigDecimal.valueOf(10), savedPortfolio.getQuantityOwned());
        assertEquals(BigDecimal.valueOf(150), savedPortfolio.getAveragePurchasePrice());
    }

    @Test
    void sellStock_shouldThrow_whenNotEnoughShares() {
        Portfolio portfolio = Portfolio.builder()
                .user(user)
                .stock(stock)
                .quantityOwned(BigDecimal.valueOf(5))
                .averagePurchasePrice(BigDecimal.valueOf(100))
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(stockRepository.findBySymbol("AAPL")).thenReturn(Optional.of(stock));
        when(portfolioRepository.findByUserAndStock(user, stock)).thenReturn(Optional.of(portfolio));

        assertThrows(InsufficientSharesException.class, () ->
                tradingService.sellStock(1L, "AAPL", BigDecimal.valueOf(10), BigDecimal.valueOf(150)));
    }

    @Test
    void buyStock_shouldThrow_whenQuantityZeroOrNegative() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(InvalidQuantityException.class, () ->
                tradingService.buyStock(1L, "AAPL", BigDecimal.valueOf(0), BigDecimal.valueOf(150)));

        assertThrows(InvalidQuantityException.class, () ->
                tradingService.buyStock(1L, "AAPL", BigDecimal.valueOf(-5), BigDecimal.valueOf(150)));
    }

    @Test
    void buyStock_shouldThrow_whenStockNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(stockRepository.findBySymbol("AAPL")).thenReturn(Optional.empty());

        assertThrows(StockNotFoundException.class, () ->
                tradingService.buyStock(1L, "AAPL", BigDecimal.valueOf(10), BigDecimal.valueOf(150)));
    }
}

