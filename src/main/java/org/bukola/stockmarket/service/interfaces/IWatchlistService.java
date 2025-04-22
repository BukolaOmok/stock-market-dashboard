package org.bukola.stockmarket.service.interfaces;

import org.bukola.stockmarket.model.Stock;
import org.bukola.stockmarket.model.Watchlist;

import java.util.List;

public interface IWatchlistService {
    List<Stock> getUserWatchlist(String userName);

    Watchlist addStockToWatchlist(String symbol, String username);

    void removeFromWatchlist(String symbol, String username);

    boolean isStockInWatchlist(String symbol, String username);
}
