package org.bukola.stockmarket.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bukola.stockmarket.configuration.AlphaVantageConfig;
import org.bukola.stockmarket.dto.alphavantage.AlphaVantageResponse;
import org.bukola.stockmarket.dto.alphavantage.GlobalQuote;
import org.bukola.stockmarket.dto.alphavantage.OverviewResponse;
import org.bukola.stockmarket.model.Stock;
import org.bukola.stockmarket.repository.StockRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockDataLoader implements CommandLineRunner {
    private final StockRepository stockRepo;
    private final RestTemplate restTemplate;
    private final AlphaVantageConfig apiConfig;

    @Override
    public void run(String... args) {
        if (stockRepo.count() == 0) {
            List<String> symbols = List.of("AAPL", "MSFT", "GOOGL");
            symbols.forEach(this::fetchAndSaveStock);
        }
    }

    private void fetchAndSaveStock(String symbol) {
        try {
            GlobalQuote quote = fetchGlobalQuote(symbol);
            OverviewResponse overview = fetchCompanyOverview(symbol);
            Stock stock = mapToStockEntity(quote, overview);
            stockRepo.save(stock);

        } catch (Exception e) {
            log.error("Failed to fetch stock: {}", symbol, e);
        }
    }

    private GlobalQuote fetchGlobalQuote(String symbol) {
        String url = String.format(
                "%s/query?function=GLOBAL_QUOTE&symbol=%s&apikey=%s",
                apiConfig.getBaseUrl(), symbol, apiConfig.getKey()
        );
        AlphaVantageResponse response = restTemplate.getForObject(url, AlphaVantageResponse.class);
        return response != null ? response.getGlobalQuote() : null;
    }

    private OverviewResponse fetchCompanyOverview(String symbol) {
        String url = String.format(
                "%s/query?function=OVERVIEW&symbol=%s&apikey=%s",
                apiConfig.getBaseUrl(), symbol, apiConfig.getKey()
        );
        return restTemplate.getForObject(url, OverviewResponse.class);
    }

    private Stock mapToStockEntity(GlobalQuote quote, OverviewResponse overview) {
        if (quote == null || overview == null) return null;

        return Stock.builder()
                .symbol(quote.getSymbol())
                .companyName(overview.getName())
                .currentPrice(quote.getPrice())
                .dayChangePercent(parsePercent(quote.getChangePercent()))
                .volume(quote.getVolume())
                .marketCap(parseMarketCap(overview.getMarketCap()))
                .peRatio(parseBigDecimal(overview.getPeRatio()))
                .sector(overview.getSector())
                .lastUpdated(LocalDateTime.now())
                .build();
    }

    private BigDecimal parsePercent(String percentStr) {
        return new BigDecimal(percentStr.replace("%", ""));
    }

    private String parseMarketCap(String marketCapStr) {
        return marketCapStr;
    }

    private BigDecimal parseBigDecimal(String numberStr) {
        return numberStr != null ? new BigDecimal(numberStr) : null;
    }
}