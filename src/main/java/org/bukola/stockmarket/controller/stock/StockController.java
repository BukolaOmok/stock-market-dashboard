package org.bukola.stockmarket.controller.stock;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.bukola.stockmarket.model.Stock;
import org.bukola.stockmarket.service.interfaces.IStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(path = "api/stocks")
@RequiredArgsConstructor
@Tag(name = "Stock API", description = "Operations on stock data")
public class StockController {

    @Autowired
    private IStockService stockService;


    @Operation(summary = "Get stock by symbol")
    @GetMapping("/{symbol}")
    public ResponseEntity<Stock> getStockBySymbol(@PathVariable String symbol) {
        return ResponseEntity.ok(stockService.getStockBySymbol(symbol));
    }

    @Operation(summary = "Get trending stocks")
    @GetMapping("/trending")
    public ResponseEntity<List<Stock>> getTrendingStocks(
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(stockService.getTrendingStocks(limit));
    }
}
