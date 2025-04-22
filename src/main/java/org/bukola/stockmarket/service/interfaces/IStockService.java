package org.bukola.stockmarket.service.interfaces;

import org.bukola.stockmarket.model.Stock;

import java.util.List;

public interface IStockService {
    Stock getStockBySymbol(String symbol);
    List<Stock> getTrendingStocks(int limit);
}
