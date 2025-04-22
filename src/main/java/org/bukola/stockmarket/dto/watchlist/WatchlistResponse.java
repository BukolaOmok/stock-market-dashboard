package org.bukola.stockmarket.dto.watchlist;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukola.stockmarket.model.Watchlist;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WatchlistResponse {
    private String symbol;
    private String companyName;
    private BigDecimal currentPrice;
    private LocalDateTime createdAt;

    public static WatchlistResponse fromEntity(Watchlist watchlist) {
        return WatchlistResponse.builder()
                .symbol(watchlist.getStock().getSymbol())
                .companyName(watchlist.getStock().getCompanyName())
                .currentPrice(watchlist.getStock().getCurrentPrice())
                .createdAt(watchlist.getCreatedAt())
                .build();
    }
}
