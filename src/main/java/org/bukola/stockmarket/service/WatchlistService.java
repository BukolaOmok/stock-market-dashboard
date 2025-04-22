package org.bukola.stockmarket.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bukola.stockmarket.model.Watchlist;
import org.bukola.stockmarket.repository.WatchlistRepository;
import org.bukola.stockmarket.service.interfaces.IWatchlistService;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class WatchlistService implements IWatchlistService {
    private final WatchlistRepository watchlistRepository;
    private final CacheManager cacheManager;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    @Cacheable(value = "stockBySymbol", key = "#symbol")
    public Watchlist getStockBySymbol(String symbol) {
        return watchlistRepository.findBySymbol(symbol.toUpperCase())
                .orElseThrow(() -> new RuntimeException("Stock not found: " + symbol));
    }
}
