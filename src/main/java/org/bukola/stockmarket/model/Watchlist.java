package org.bukola.stockmarket.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "watchlists",
indexes = {
        @Index(name = "idx_watchlist_user_stock",
                columnList = "user_id, stock_id",
                unique = true),

        @Index(name = "idx_watchlist_user",
                columnList = "user_id")
})
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Watchlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;

    private LocalDateTime createdAt = LocalDateTime.now();
}
