package org.bukola.stockmarket.controller.stock;

import lombok.RequiredArgsConstructor;
import org.bukola.stockmarket.dto.portfolio.PortfolioDto;
import org.bukola.stockmarket.dto.transactions.TransactionsDto;
import org.bukola.stockmarket.model.Portfolio;
import org.bukola.stockmarket.model.Transaction;
import org.bukola.stockmarket.service.interfaces.ITradingService;
import org.bukola.stockmarket.repository.PortfolioRepository;
import org.bukola.stockmarket.repository.TransactionRepository;
import org.bukola.stockmarket.repository.UserRepository;
import org.bukola.stockmarket.repository.StockRepository;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/portfolio")
@RequiredArgsConstructor
public class TransactionsController {

    private final ITradingService tradingService;
    private final PortfolioRepository portfolioRepository;
    private final UserRepository userRepository;

    // Buy stock
    @PostMapping("/buy")
    public void buyStock(@RequestParam String symbol,
                         @RequestParam BigDecimal quantity,
                         @RequestParam BigDecimal pricePerShare,
                         Principal principal) {

        Long userId = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();

        tradingService.buyStock(userId, symbol, quantity, pricePerShare);
    }

    // Sell stock
    @PostMapping("/sell")
    public void sellStock(@RequestParam String symbol,
                          @RequestParam BigDecimal quantity,
                          @RequestParam BigDecimal pricePerShare,
                          Principal principal) {

        Long userId = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();

        tradingService.sellStock(userId, symbol, quantity, pricePerShare);
    }

    // Get portfolio for current user
    @GetMapping("/")
    public List<PortfolioDto> getPortfolio(Principal principal) {
        var user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Portfolio> portfolios = portfolioRepository.findByUser(user);

        return portfolios.stream().map(p -> PortfolioDto.builder()
                        .username(user.getUsername())
                        .stockSymbol(p.getStock().getSymbol())
                        .quantityOwned(p.getQuantityOwned())
                        .averagePurchasePrice(p.getAveragePurchasePrice())
                        .build())
                .collect(Collectors.toList());
    }
}
