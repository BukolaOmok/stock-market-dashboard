package org.bukola.stockmarket.repository;

import org.bukola.stockmarket.model.Transaction;
import org.bukola.stockmarket.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUser(User user);
}
