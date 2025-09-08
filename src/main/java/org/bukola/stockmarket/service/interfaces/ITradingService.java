package org.bukola.stockmarket.service.interfaces;

import java.math.BigDecimal;

public interface ITradingService {
    void buyStock(Long userId, String symbol, BigDecimal quantity, BigDecimal pricePerShare);
    void sellStock(Long userId, String symbol, BigDecimal quantity, BigDecimal pricePerShare);
}
