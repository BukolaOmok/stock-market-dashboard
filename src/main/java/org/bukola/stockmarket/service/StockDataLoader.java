package org.bukola.stockmarket.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bukola.stockmarket.dto.twelvedata.TwelveDataResponse;
import org.bukola.stockmarket.model.Stock;
import org.bukola.stockmarket.repository.StockRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
            TwelveDataResponse response = fetchFromTwelveData(symbol);
            Stock stock = mapToStockEntity(response);

            stockRepo.findBySymbol(symbol).ifPresent(existing -> stock.setId(existing.getId()));
            stockRepo.save(stock);

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
                .marketCap(response.getMarket_cap())
                .sector(response.getSector())
                .lastUpdated(LocalDateTime.now())
                .build();
    }
}