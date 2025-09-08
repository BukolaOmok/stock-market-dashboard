package org.bukola.stockmarket.dto.portfolio;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PortfolioDto {
    private String username;
    private String stockSymbol;
    private BigDecimal quantityOwned;
    private BigDecimal averagePurchasePrice;
}
