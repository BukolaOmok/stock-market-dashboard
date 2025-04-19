package org.bukola.stockmarket.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "stocks")
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true, length = 10)
    private String symbol;

    @Column(nullable = false, length = 100)
    private String companyName;

    @Column(precision = 10, scale = 2)
    private BigDecimal currentPrice;

    @Column(precision = 5, scale = 2)
    private BigDecimal dayChangePercent;

    private Long volume;

    private String marketCap;

    @Column(precision = 10, scale = 2)
    private BigDecimal peRatio;

    @Column(length = 50)
    private String sector;

    private LocalDateTime lastUpdated;
}
