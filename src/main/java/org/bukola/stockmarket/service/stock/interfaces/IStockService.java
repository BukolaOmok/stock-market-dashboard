package org.bukola.stockmarket.service.stock.interfaces;

import org.bukola.stockmarket.model.Stock;

import java.util.List;

public interface IStockService {
    Stock getStockBySymbol(String symbol);
    List<Stock> getTrendingStocks(int limit);
}
