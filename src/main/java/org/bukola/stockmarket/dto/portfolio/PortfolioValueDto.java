package org.bukola.stockmarket.dto.portfolio;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PortfolioValueDto {
    private String stockSymbol;
    private BigDecimal quantityOwned;
    private BigDecimal averagePurchasePrice;
    private BigDecimal currentPrice;
    private BigDecimal totalValue;
    private BigDecimal profitLossPercentage;
}

