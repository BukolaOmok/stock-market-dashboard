package org.bukola.stockmarket.dto.transactions;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class TransationsDto {
    private Long id;
    private String userName;
    private String stockSymbol;
    private TransactionType transactionType;
    private BigDecimal quantity;
    private BigDecimal pricePerShare;
    private LocalDateTime transactionTimeStamp;
}
