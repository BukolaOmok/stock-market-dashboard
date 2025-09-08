package org.bukola.stockmarket.dto.transactions;

import lombok.Builder;
import lombok.Data;
import org.bukola.stockmarket.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class TransactionsDto {
    private Long id;
    private String username;
    private String stockSymbol;
    private TransactionType transactionType;
    private BigDecimal quantity;
    private BigDecimal pricePerShare;
    private LocalDateTime transactionTimestamp;

}
