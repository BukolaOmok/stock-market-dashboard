package org.bukola.stockmarket.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bukola.stockmarket.dto.twelvedata.TwelveDataResponse;
import org.bukola.stockmarket.model.Stock;
import org.bukola.stockmarket.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockDataLoader implements CommandLineRunner {
    private static final List<String> SYMBOLS = List.of("AAPL", "MSFT", "GOOGL", "AMZN", "META");

    private final StockRepository stockRepo;
    private final RestTemplate restTemplate;
    private final SimpMessagingTemplate messagingTemplate;

    @Value("${twelvedata.base.url}")
    private String baseUrl;

    @Value("${twelvedata.api.key}")
    private String apiKey;

    @Override
    public void run(String... args) {
        if (stockRepo.count() == 0) {
            fetchAllStocks();
        } else {
            SYMBOLS.forEach(symbol -> {
                stockRepo.findBySymbol(symbol).ifPresent(stock -> {
                    Objects.requireNonNull(cacheManager.getCache("stocks")).put(symbol, stock);
                    log.debug("Pre-cached {}", symbol);
                });
            });
        }
    }

    @Scheduled(fixedRate = 300_000) // 5 minutes
    public void refreshStocks() {
        fetchAllStocks();
    }

    private void fetchAllStocks() {
        SYMBOLS.forEach(symbol -> {
            try {
                fetchAndSaveStock(symbol);
                Thread.sleep(1000); // 1 second delay between requests
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    private void fetchAndSaveStock(String symbol) {
            try {
                // First try to get from cache
                Stock cachedStock = getCachedStock(symbol);
                if (cachedStock != null &&
                        cachedStock.getLastUpdated().isAfter(LocalDateTime.now().minusMinutes(1))) {
                    messagingTemplate.convertAndSend("/topic/stocks", cachedStock);
                    return;
                }

                TwelveDataResponse response = fetchFromTwelveData(symbol);
                Stock stock = mapToStockEntity(response);

                stockRepo.findBySymbol(symbol).ifPresent(existing -> stock.setId(existing.getId()));
                stockRepo.save(stock);
                cacheManager.getCache("stocks").put(symbol, stock);

                messagingTemplate.convertAndSend("/topic/stocks", stock);
                log.info("Updated: {}", symbol);

            } catch (Exception e) {
                log.error("Failed to fetch {}: {}", symbol, e.getMessage());
            }
        }

    private TwelveDataResponse fetchFromTwelveData(String symbol) {
        String url = String.format(
                "%s/quote?symbol=%s&apikey=%s",
                baseUrl, symbol, apiKey
        );

        log.debug("Fetching: {}", url);
        return restTemplate.getForObject(url, TwelveDataResponse.class);
    }

    private Stock mapToStockEntity(TwelveDataResponse response) {
        return Stock.builder()
                .symbol(response.getSymbol())
                .companyName(response.getName())
                .currentPrice(new BigDecimal(response.getPrice()))
                .dayChangePercent(new BigDecimal(response.getChangePercent().replace("%", "")))
                .volume(Long.parseLong(response.getVolume()))
                .lastUpdated(LocalDateTime.now())
                .build();
    }

    @Autowired
    private CacheManager cacheManager;

    public Map<String, String> getCacheStats () {
        CaffeineCache cache = (CaffeineCache) cacheManager.getCache("stocks");
        assert cache != null;
        com.github.benmanes.caffeine.cache.stats.CacheStats stats = cache.getNativeCache().stats();

        return Map.of(
                "hitRate", String.format("%.2f%%", stats.hitRate() * 100),
                "missRate", String.format("%.2f%%", stats.missRate() * 100),
                "loadCount", String.valueOf(stats.loadCount()),
                "evictionCount", String.valueOf(stats.evictionCount())
        );
    }

    @Scheduled(fixedRate = 60_000)  // Log stats every minute
    public void logCacheStats() {
        log.info("Cache Stats: {}", getCacheStats());
    }

    @Scheduled(fixedRate = 30_000)
    public void refreshHotItems() {
        log.debug("Refreshing hot items...");
        getCacheAccessStats().entrySet().stream()
                .filter(entry -> entry.getValue() > 5) // Threshold for "hot" items
                .peek(entry -> log.debug("Hot item detected: {} ({} accesses)",
                        entry.getKey(), entry.getValue()))
                .forEach(entry -> {
                    String symbol = entry.getKey();
                    cacheManager.getCache("stocks").evict(symbol);
                    fetchAndSaveStock(symbol); // Re-populate cache
                });

        cacheAccessTracker.resetStats();
    }


    private static class CacheAccessTracker {
        private final Map<String, AtomicInteger> accessCounts = new ConcurrentHashMap<>();

        public void recordAccess(String key) {
            accessCounts.computeIfAbsent(key, k -> new AtomicInteger()).incrementAndGet();
        }

        public Map<String, Integer> getAccessCounts() {
            return accessCounts.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            e -> e.getValue().get()
                    ));
        }

        public void resetStats() {
            accessCounts.clear();
        }
    }

    private final CacheAccessTracker cacheAccessTracker = new CacheAccessTracker();

    public Map<String, Integer> getCacheAccessStats() {
        Map<String, Integer> stats = new HashMap<>();

        CaffeineCache cache = (CaffeineCache) cacheManager.getCache("stocks");
        if (cache != null) {
            cache.getNativeCache().asMap().keySet().forEach(key -> {
                String symbol = key.toString();
                stats.put(symbol, cacheAccessTracker.getAccessCounts().getOrDefault(symbol, 0));
            });
        }

        return stats;
    }

    private Stock getCachedStock(String symbol) {
        cacheAccessTracker.recordAccess(symbol);
        return cacheManager.getCache("stocks").get(symbol, Stock.class);
    }

    @PostConstruct
    public void initCacheWrapper() {
        CaffeineCache cache = (CaffeineCache) cacheManager.getCache("stocks");
        if (cache != null) {
            cache.getNativeCache().asMap().forEach((key, value) -> {
                cacheAccessTracker.recordAccess(key.toString());
            });
        }
    }
}