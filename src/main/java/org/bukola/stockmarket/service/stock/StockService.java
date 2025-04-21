package org.bukola.stockmarket.service.stock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bukola.stockmarket.model.Stock;
import org.bukola.stockmarket.repository.StockRepository;
import org.bukola.stockmarket.service.stock.interfaces.IStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class StockService implements IStockService {

    private StockRepository stockRepository;
    private CacheManager cacheManager;
    private SimpMessagingTemplate messagingTemplate;


    @Override
    @Cacheable(value = "stockBySymbol", key = "#symbol")
    public Stock getStockBySymbol(String symbol) {
        return stockRepository.findBySymbol(symbol.toUpperCase())
                .orElseThrow(() -> new RuntimeException("Stock not found: " + symbol));
    }

    @Override
    public List<Stock> getTrendingStocks(int limit) {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        return stockRepository.findTrendingStocks(oneHourAgo)
                .stream()
                .limit(limit)
                .collect(Collectors.toList());
    }
}
