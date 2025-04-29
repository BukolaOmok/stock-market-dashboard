package org.bukola.stockmarket.repository;

import org.bukola.stockmarket.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    Optional<Stock> findBySymbol(String symbol);

    @Query("SELECT s FROM Stock s WHERE s.lastUpdated >= :since " +
            "ORDER BY (s.volume * ABS(s.dayChangePercent)) DESC")
    List<Stock> findTrendingStocks(@Param("since") LocalDateTime since);
}
