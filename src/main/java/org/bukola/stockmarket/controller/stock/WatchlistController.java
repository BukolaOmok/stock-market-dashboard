package org.bukola.stockmarket.controller.stock;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.bukola.stockmarket.dto.watchlist.WatchlistRequest;
import org.bukola.stockmarket.dto.watchlist.WatchlistResponse;
import org.bukola.stockmarket.model.Stock;
import org.bukola.stockmarket.model.User;
import org.bukola.stockmarket.model.Watchlist;
import org.bukola.stockmarket.service.interfaces.IWatchlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping(path = "api/watchlist")
@RequiredArgsConstructor
@Tag(name = "Stock API", description = "Operations on stock data")
public class WatchlistController {

    @Autowired
    private IWatchlistService watchlistService;


    @Operation(summary = "Retrieve all stocks in user's watchlist")
    @GetMapping
    public ResponseEntity<List<WatchlistResponse>> getWatchlist(
            @AuthenticationPrincipal User user) {

        List<Stock> stocks = watchlistService.getUserWatchlist(user.getUsername());

        List<WatchlistResponse> response = stocks.stream()
                .map(stock -> WatchlistResponse.builder()
                        .symbol(stock.getSymbol())
                        .companyName(stock.getCompanyName())
                        .currentPrice(stock.getCurrentPrice())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Add a stock to user's watchlist")
    @PostMapping("/add/{symbol}")
     public ResponseEntity<WatchlistResponse> addStockToWatchlist(
            @RequestBody WatchlistRequest request,
            @AuthenticationPrincipal User user) {

        Watchlist watchlist = watchlistService.addStockToWatchlist(
                request.getSymbol(),
                user.getUsername());

        return ResponseEntity.ok(WatchlistResponse.fromEntity(watchlist));
    }

    @Operation(summary = "Check if stock is in watchlist")
    @GetMapping("/contains/{symbol}")
    public ResponseEntity<Boolean> isStockInWatchlist(
            @PathVariable String symbol,
            @AuthenticationPrincipal User user) {

        boolean exists = watchlistService.isStockInWatchlist(
                symbol,
                user.getUsername());
        return ResponseEntity.ok(exists);
    }

    @Operation(summary = "Remove a stock from watchlist")
    @DeleteMapping("/remove/{symbol}")
    public ResponseEntity<Void> removeFromWatchlist(
            @PathVariable String symbol,
            @AuthenticationPrincipal User user) {

        watchlistService.removeFromWatchlist(symbol, user.getUsername());
        return ResponseEntity.noContent().build();
    }

}
