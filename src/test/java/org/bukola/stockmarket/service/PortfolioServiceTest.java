package org.bukola.stockmarket.service;

import org.bukola.stockmarket.dto.portfolio.PortfolioValueDto;
import org.bukola.stockmarket.model.Portfolio;
import org.bukola.stockmarket.model.Stock;
import org.bukola.stockmarket.model.User;
import org.bukola.stockmarket.repository.PortfolioRepository;
import org.bukola.stockmarket.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PortfolioServiceTest {

    @Mock
    private PortfolioRepository portfolioRepository;

    @InjectMocks
    private PortfolioService portfolioService;

    @Mock
    private UserRepository userRepository;


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

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    }

    @Test
    void getPortfolioValues_shouldCalculateTotalAndProfitLoss() {
        Portfolio portfolio = Portfolio.builder()
                .user(user)
                .stock(stock)
                .quantityOwned(BigDecimal.valueOf(10))
                .averagePurchasePrice(BigDecimal.valueOf(100))
                .build();

        when(portfolioRepository.findByUser(user)).thenReturn(List.of(portfolio));

        List<PortfolioValueDto> values = portfolioService.getPortfolioValues(user.getId());

        PortfolioValueDto dto = values.get(0);
        assertEquals(BigDecimal.valueOf(1500), dto.getTotalValue()); // 10*150
        assertEquals(0, dto.getProfitLossPercentage().compareTo(BigDecimal.valueOf(50))); // ((150-100)/100)*100
    }

}
