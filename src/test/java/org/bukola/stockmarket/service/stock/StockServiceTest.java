package org.bukola.stockmarket.service.stock;

import org.bukola.stockmarket.model.Stock;
import org.bukola.stockmarket.repository.StockRepository;
import org.bukola.stockmarket.service.StockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock
    private StockRepository stockRepository;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private StockService stockService;

    private final Stock testStock = Stock.builder()
            .symbol("AAPL")
            .companyName("Apple Inc.")
            .currentPrice(new BigDecimal("175.50"))
            .build();

    @BeforeEach
    void setUp() {}

    @Test
    void getStockBySymbol_shouldReturnStock_whenStockExists() {
        when(stockRepository.findBySymbol("AAPL")).thenReturn(Optional.of(testStock));

        Stock result = stockService.getStockBySymbol("AAPL");

        assertNotNull(result);
        assertEquals("AAPL", result.getSymbol());
        verify(stockRepository).findBySymbol("AAPL");
    }

    @Test
    void getStockBySymbol_shouldThrowException_whenStockNotFound() {
        when(stockRepository.findBySymbol("INVALID")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> stockService.getStockBySymbol("INVALID"));
        verify(stockRepository).findBySymbol("INVALID");
    }

    @Test
    void getTrendingStocks_shouldReturnLimitedStocks() {
        LocalDateTime testTime = LocalDateTime.of(2025, 4, 21, 12, 0);
        List<Stock> mockStocks = List.of(
                Stock.builder().symbol("AAPL").volume(1000000L).build(),
                Stock.builder().symbol("MSFT").volume(800000L).build(),
                Stock.builder().symbol("GOOGL").volume(750000L).build()
        );

        when(stockRepository.findTrendingStocks(any(LocalDateTime.class)))
                .thenReturn(mockStocks);

        List<Stock> result = stockService.getTrendingStocks(2);

        assertEquals(2, result.size());
        assertEquals("AAPL", result.get(0).getSymbol());
        assertEquals("MSFT", result.get(1).getSymbol());
    }

    @Test
    void getTrendingStocks_shouldReturnEmptyList_whenNoStocksFound() {
        when(stockRepository.findTrendingStocks(any(LocalDateTime.class)))
                .thenReturn(List.of());

        List<Stock> result = stockService.getTrendingStocks(5);

        assertTrue(result.isEmpty());
    }
}