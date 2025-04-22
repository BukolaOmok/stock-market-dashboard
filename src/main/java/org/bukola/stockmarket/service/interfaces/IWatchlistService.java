package org.bukola.stockmarket.service.interfaces;

import org.bukola.stockmarket.model.Watchlist;
import org.springframework.cache.annotation.Cacheable;

public interface IWatchlistService {
    Watchlist getStockBySymbol(String symbol);
}
