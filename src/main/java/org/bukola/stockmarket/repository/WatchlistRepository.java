package org.bukola.stockmarket.repository;

import org.bukola.stockmarket.model.Stock;
import org.bukola.stockmarket.model.Watchlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WatchlistRepository extends JpaRepository<Watchlist, Long> {
    Optional<Watchlist> findBySymbol(String symbol);
}
