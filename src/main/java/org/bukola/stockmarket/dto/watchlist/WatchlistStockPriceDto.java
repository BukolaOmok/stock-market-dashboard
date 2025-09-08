package org.bukola.stockmarket.dto.watchlist;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class WatchlistStockPriceDto {
    private String symbol;
    private String companyName;
    private BigDecimal currentPrice;
    private BigDecimal dayChangePercent;
    private Long volume;
    private LocalDateTime lastUpdated;
    private boolean isRealtime;
}
