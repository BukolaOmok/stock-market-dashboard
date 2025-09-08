package org.bukola.stockmarket.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bukola.stockmarket.dto.watchlist.WatchlistStockPriceDto;
import org.bukola.stockmarket.model.Stock;
import org.bukola.stockmarket.model.User;
import org.bukola.stockmarket.model.Watchlist;
import org.bukola.stockmarket.repository.UserRepository;
import org.bukola.stockmarket.repository.WatchlistRepository;
import org.bukola.stockmarket.service.interfaces.IStockService;
import org.bukola.stockmarket.service.interfaces.IWatchlistService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class WatchlistService implements IWatchlistService {
    private final WatchlistRepository watchlistRepository;
    private final UserRepository userRepository;
    private final IStockService stockService;
    private final StockDataLoader stockDataLoader;

    @Override
    public List<Stock> getUserWatchlist(String username) {
        User user = getUser(username);
        List<Stock> stocks = watchlistRepository.findStocksByUser(user);

        return stocks.stream()
                .map(this::enhanceWithRealTimeData)
                .collect(Collectors.toList());
    }

    public List<WatchlistStockPriceDto> getWatchlistWithPrices(String username) {
        User user = getUser(username);
        return watchlistRepository.findStocksByUser(user).stream()
                .map(this::mapToPriceDto)
                .collect(Collectors.toList());
    }

    private Stock enhanceWithRealTimeData(Stock stock) {
        Stock freshData = stockDataLoader.getCachedStock(stock.getSymbol());
        if (freshData != null && isFresh(freshData)) {
            return freshData;
        }

        return stock;
    }

    private WatchlistStockPriceDto mapToPriceDto(Stock stock) {
        Stock enhancedStock = enhanceWithRealTimeData(stock);
        return WatchlistStockPriceDto.builder()
                .symbol(enhancedStock.getSymbol())
                .companyName(enhancedStock.getCompanyName())
                .currentPrice(enhancedStock.getCurrentPrice())
                .dayChangePercent(enhancedStock.getDayChangePercent())
                .volume(enhancedStock.getVolume())
                .lastUpdated(enhancedStock.getLastUpdated())
                .isRealtime(isFresh(enhancedStock))
                .build();
    }

    private boolean isFresh(Stock stock) {
        return stock.getLastUpdated().isAfter(LocalDateTime.now().minusMinutes(5));
    }

    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    @Override
    public Watchlist addStockToWatchlist(String symbol, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Stock stock = stockService.getStockBySymbol(symbol);

        if (watchlistRepository.existsByUserAndStock(user, stock)) {
            throw new IllegalStateException("Stock already in watchlist");
        }

        Watchlist watchlist = Watchlist.builder()
                .stock(stock)
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();

        return watchlistRepository.save(watchlist);
    }

    @Override
    public void removeFromWatchlist(String symbol, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Stock stock = stockService.getStockBySymbol(symbol);

        if (!watchlistRepository.existsByUserAndStock(user, stock)) {
            throw new EntityNotFoundException("Stock not found in watchlist");
        }

        watchlistRepository.deleteByUserAndStock(user, stock);
    }

    @Override
    public boolean isStockInWatchlist(String username, String symbol) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Stock stock = stockService.getStockBySymbol(symbol);

        return watchlistRepository.existsByUserAndStock(user, stock);
    }
}
