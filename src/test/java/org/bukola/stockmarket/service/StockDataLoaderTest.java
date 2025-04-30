package org.bukola.stockmarket.service;

import org.bukola.stockmarket.dto.twelvedata.TwelveDataResponse;
import org.bukola.stockmarket.model.Stock;
import org.bukola.stockmarket.repository.StockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class StockDataLoaderTest {

    @Mock private StockRepository stockRepo;
    @Mock private CacheManager cacheManager;
    @Mock private Cache cache;
    @Mock private RestTemplate restTemplate;
    @Mock private SimpMessagingTemplate messagingTemplate;

    private StockDataLoader stockDataLoader;

    @BeforeEach
    void setUp() {
        stockDataLoader = new StockDataLoader(
                stockRepo,
                restTemplate,
                messagingTemplate,
                cacheManager
        );
    }

    @Test
    public void testCacheHit_shouldNotAccessDb() {
        Stock cachedStock = new Stock();
        cachedStock.setSymbol("AAPL");
        cachedStock.setLastUpdated(LocalDateTime.now());

        when(cacheManager.getCache("stocks")).thenReturn(cache);
        when(cache.get("AAPL", Stock.class)).thenReturn(cachedStock);

        stockDataLoader.fetchAndSaveStock("AAPL");

        verify(cacheManager).getCache("stocks");
        verify(cache).get("AAPL", Stock.class);
        verify(messagingTemplate).convertAndSend("/topic/stocks", cachedStock);
        verifyNoInteractions(stockRepo, restTemplate);
    }

    @Test
    public void testCacheMissWithFreshDbData_shouldUseDbData() {
        Stock freshStock = new Stock();
        freshStock.setSymbol("AAPL");
        freshStock.setLastUpdated(LocalDateTime.now().minusMinutes(4));

        when(cacheManager.getCache("stocks")).thenReturn(cache);
        when(cache.get("AAPL", Stock.class)).thenReturn(null);
        when(stockRepo.findBySymbol("AAPL")).thenReturn(Optional.of(freshStock));

        stockDataLoader.fetchAndSaveStock("AAPL");

        verify(cache).put("AAPL", freshStock);
        verify(messagingTemplate).convertAndSend("/topic/stocks", freshStock);
        verifyNoInteractions(restTemplate);
        verify(stockRepo, never()).save(any());
    }

    @Test
    public void testCacheMissWithStaleDbData_shouldFetchFromApi() {
        Stock staleStock = new Stock();
        staleStock.setSymbol("AAPL");
        staleStock.setLastUpdated(LocalDateTime.now().minusMinutes(6));
        staleStock.setId(1L);

        TwelveDataResponse apiResponse = new TwelveDataResponse();
        apiResponse.setSymbol("AAPL");
        apiResponse.setPrice("150.00");
        apiResponse.setChangePercent("1.5%");
        apiResponse.setVolume("1000000");
        apiResponse.setName("Apple Inc.");

        when(cacheManager.getCache("stocks")).thenReturn(cache);
        when(cache.get("AAPL", Stock.class)).thenReturn(null);
        when(stockRepo.findBySymbol("AAPL")).thenReturn(Optional.of(staleStock));
        when(restTemplate.getForObject(anyString(), eq(TwelveDataResponse.class)))
                .thenReturn(apiResponse);

        stockDataLoader.fetchAndSaveStock("AAPL");

        verify(restTemplate).getForObject(anyString(), eq(TwelveDataResponse.class));

        ArgumentCaptor<Stock> stockCaptor = ArgumentCaptor.forClass(Stock.class);
        verify(stockRepo).save(stockCaptor.capture());

        Stock savedStock = stockCaptor.getValue();
        assertEquals(1L, savedStock.getId());
        assertEquals(new BigDecimal("150.00"), savedStock.getCurrentPrice());

        verify(cache).put(eq("AAPL"), any(Stock.class));
    }
}