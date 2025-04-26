package org.bukola.stockmarket.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Service
@Slf4j
@RequiredArgsConstructor
public class WatchlistService implements IWatchlistService {
    private final WatchlistRepository watchlistRepository;
    private final UserRepository userRepository;
    private final IStockService stockService;

    @Override
    public List<Stock> getUserWatchlist(String userName) {
        User user = userRepository.findByUsername(userName).orElseThrow(() -> new RuntimeException("User not found"));
        return watchlistRepository.findStocksByUser(user);
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
    public boolean isStockInWatchlist(String symbol, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Stock stock = stockService.getStockBySymbol(symbol);

        return watchlistRepository.existsByUserAndStock(user, stock);
    }
}
