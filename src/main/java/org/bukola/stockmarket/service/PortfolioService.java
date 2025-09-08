package org.bukola.stockmarket.service;

import lombok.RequiredArgsConstructor;
import org.bukola.stockmarket.dto.portfolio.PortfolioValueDto;
import org.bukola.stockmarket.model.Portfolio;
import org.bukola.stockmarket.model.User;
import org.bukola.stockmarket.repository.PortfolioRepository;
import org.bukola.stockmarket.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PortfolioService {
    private final UserRepository userRepository;
    private final PortfolioRepository portfolioRepository;

    public List<PortfolioValueDto> getPortfolioValues(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Portfolio> portfolios = portfolioRepository.findByUser(user);

        return portfolios.stream().map(p -> {
            BigDecimal currentPrice = p.getStock().getCurrentPrice();
            BigDecimal totalValue = currentPrice.multiply(p.getQuantityOwned());

            BigDecimal purchaseTotal = p.getAveragePurchasePrice().multiply(p.getQuantityOwned());
            BigDecimal profitLoss = totalValue.subtract(purchaseTotal);
            BigDecimal profitLossPercent = profitLoss.divide(purchaseTotal, 2, BigDecimal.ROUND_HALF_UP)
                    .multiply(BigDecimal.valueOf(100));

            return PortfolioValueDto.builder()
                    .stockSymbol(p.getStock().getSymbol())
                    .quantityOwned(p.getQuantityOwned())
                    .averagePurchasePrice(p.getAveragePurchasePrice())
                    .currentPrice(currentPrice)
                    .totalValue(totalValue)
                    .profitLossPercentage(profitLossPercent)
                    .build();
        }).collect(Collectors.toList());
    }

}
