package org.bukola.stockmarket.controller.stock;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.bukola.stockmarket.model.Stock;
import org.bukola.stockmarket.service.interfaces.IWatchlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(path = "api/watchlist")
@RequiredArgsConstructor
@Tag(name = "Stock API", description = "Operations on stock data")
public class WatchlistController {

    @Autowired
    private IWatchlistService watchlistService;


    @Operation(summary = "Retrieve all stocks in user's watchlist")
    @GetMapping
    public ResponseEntity<Stock> getStocksInWatchlist(@PathVariable String symbol) {
        return ResponseEntity.ok(watchlistService.getStocksInWatchlist(symbol));
    }

    @Operation(summary = "Add a stock to user's watchlist")
    @PostMapping("/add")
    public ResponseEntity<List<Stock>> addStockToWatchlist(
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(watchlistService.addStockToWatchlist(limit));
    }

    @Operation(summary = "Remove a stock from watchlist")
    @DeleteMapping("/remove/{symbol}")
    public ResponseEntity<List<Stock>> removeStockFromWatchlist(
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(watchlistService.removeStockFromWatchlist(limit));
    }
}
