package org.bukola.stockmarket.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transactions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;

    private String transactionType;

    private BigDecimal quantity;

    private BigDecimal pricePerShare;

    private LocalDateTime transactionTimeStamp;
}
