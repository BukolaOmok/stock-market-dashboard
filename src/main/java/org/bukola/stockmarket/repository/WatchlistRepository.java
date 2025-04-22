package org.bukola.stockmarket.repository;

import jakarta.transaction.Transactional;
import org.bukola.stockmarket.model.Stock;
import org.bukola.stockmarket.model.User;
import org.bukola.stockmarket.model.Watchlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WatchlistRepository extends JpaRepository<Watchlist, Long> {
    boolean existsByUserAndStock(User user, Stock stock);
    List<Watchlist> findByUser(User user);
    Optional<Watchlist> findByUserAndStock(User user, Stock stock);

    @Transactional
    void deleteByUserAndStock(User user, Stock stock);

    @Query("SELECT w.stock FROM Watchlist w WHERE w.user = :user")
    List<Stock> findStocksByUser(@Param("user") User user);
}
