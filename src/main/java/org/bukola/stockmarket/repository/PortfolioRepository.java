package org.bukola.stockmarket.repository;

import org.bukola.stockmarket.model.Portfolio;
import org.bukola.stockmarket.model.Stock;
import org.bukola.stockmarket.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    Optional<Portfolio> findByUserAndStock(User user, Stock stock);
    List<Portfolio> findByUser(User user);
}
