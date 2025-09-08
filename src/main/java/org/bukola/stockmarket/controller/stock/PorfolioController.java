package org.bukola.stockmarket.controller.stock;

import lombok.RequiredArgsConstructor;
import org.bukola.stockmarket.dto.transactions.TransactionsDto;
import org.bukola.stockmarket.model.Transaction;
import org.bukola.stockmarket.repository.TransactionRepository;
import org.bukola.stockmarket.repository.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transaction")
@RequiredArgsConstructor
public class PorfolioController {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    // Get historical transactions
    @GetMapping("/")
    public List<TransactionsDto> getTransactions(Principal principal) {
        var user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Transaction> transactions = transactionRepository.findByUser(user);

        return transactions.stream().map(tx -> TransactionsDto.builder()
                        .id(tx.getId())
                        .username(user.getUsername())
                        .stockSymbol(tx.getStock().getSymbol())
                        .transactionType(tx.getTransactionType())
                        .quantity(tx.getQuantity())
                        .pricePerShare(tx.getPricePerShare())
                        .transactionTimestamp(tx.getTransactionTimestamp())
                        .build())
                .collect(Collectors.toList());
    }
}
