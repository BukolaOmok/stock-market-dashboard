package org.bukola.stockmarket.service;

import jakarta.persistence.EntityNotFoundException;
import org.bukola.stockmarket.enums.Role;
import org.bukola.stockmarket.model.Stock;
import org.bukola.stockmarket.model.User;
import org.bukola.stockmarket.model.Watchlist;
import org.bukola.stockmarket.repository.UserRepository;
import org.bukola.stockmarket.repository.WatchlistRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Given user has a watchlist")
@Nested
public class WatchlistServiceTest {
    @Mock
    WatchlistRepository watchlistRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    StockService stockService;

    @InjectMocks
    WatchlistService watchlistService;

    @Nested
    @DisplayName("When user tries to add a stock to the watchlist")
    class AddStockToWatchlist {
        @Test
        @DisplayName("Then user cannot add stock that already exists in watchlist")
        void addStockToWatchlist_throwsException_whenStocksExistsInWatchList() {
            User user = new User("Bukola", "$2a$10$encodedPassword", Role.USER);
            Stock stockInWatchlist = new Stock(2,	"AAPL", "Apple Inc.", BigDecimal.valueOf(196.98),	BigDecimal.valueOf(1.39),	51334300L, LocalDateTime.of(2025, 4, 21, 13, 53));

            when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
            when(stockService.getStockBySymbol(stockInWatchlist.getSymbol())).thenReturn(stockInWatchlist);
            when(watchlistRepository.existsByUserAndStock(user, stockInWatchlist)).thenReturn(true);

            assertThrows(IllegalStateException.class, () -> watchlistService.addStockToWatchlist(stockInWatchlist.getSymbol(), user.getUsername()));

            verify(watchlistRepository, never()).save(any());
        }

        @Test
        @DisplayName("Then stock is added when not already existing in watchlist")
        void addStockToWatchlist_succeeds_whenStockNotInWatchlist() {
            User user = new User("Bukola", "$2a$10$encodedPassword", Role.USER);
            Stock stockInWatchlist = new Stock(2,	"AAPL", "Apple Inc.", BigDecimal.valueOf(196.98),	BigDecimal.valueOf(1.39),	51334300L, LocalDateTime.of(2025, 4, 21, 13, 53));

            when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
            when(stockService.getStockBySymbol(stockInWatchlist.getSymbol())).thenReturn(stockInWatchlist);
            when(watchlistRepository.existsByUserAndStock(user, stockInWatchlist)).thenReturn(false);
            when(watchlistRepository.save(any(Watchlist.class))).thenAnswer(invocation -> invocation.getArgument(0));

            watchlistService.addStockToWatchlist(stockInWatchlist.getSymbol(), user.getUsername());

            verify(watchlistRepository).existsByUserAndStock(user, stockInWatchlist);
            ArgumentCaptor<Watchlist> captor = ArgumentCaptor.forClass(Watchlist.class);
            verify(watchlistRepository).save(captor.capture());

            Watchlist savedStock = captor.getValue();
            assertEquals(user, savedStock.getUser());
            assertEquals(stockInWatchlist, savedStock.getStock());
        }
    }

    @Nested
    @DisplayName("When user tries to remove a stock from the watchlist")
    class RemoveStockFromWatchlist {
        @Test
        @DisplayName("Then user cannot remove the stock if it does not already exists in the watchlist")
        void removeStockFromWatchlist_throwsException_whenStockNotInWatchlist() {
            User user = new User("Bukola", "$2a$10$encodedPassword", Role.USER);
            Stock stockInWatchlist = new Stock(2,	"AAPL", "Apple Inc.", BigDecimal.valueOf(196.98),	BigDecimal.valueOf(1.39),	51334300L, LocalDateTime.of(2025, 4, 21, 13, 53));

            when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
            when(stockService.getStockBySymbol(stockInWatchlist.getSymbol())).thenReturn(stockInWatchlist);
            when(watchlistRepository.existsByUserAndStock(user, stockInWatchlist)).thenReturn(false);

            assertThrows(EntityNotFoundException.class, () -> watchlistService.removeFromWatchlist(stockInWatchlist.getSymbol(), user.getUsername()));

            verify(watchlistRepository, never()).delete(any());
        }

        @Test
        @DisplayName("Then user can remove stock if it exists in the watchlist")
        void removeStockFromWatchlist_succeeds_whenStockExistsInWatchList () {
            User user = new User("Bukola", "$2a$10$encodedPassword", Role.USER);
            Stock stockInWatchlist = new Stock(2,	"AAPL", "Apple Inc.", BigDecimal.valueOf(196.98),	BigDecimal.valueOf(1.39),	51334300L, LocalDateTime.of(2025, 4, 21, 13, 53));

            when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
            when(stockService.getStockBySymbol(stockInWatchlist.getSymbol())).thenReturn(stockInWatchlist);
            when(watchlistRepository.existsByUserAndStock(user, stockInWatchlist)).thenReturn(true);

            watchlistService.removeFromWatchlist(stockInWatchlist.getSymbol(), user.getUsername());

            verify(watchlistRepository).deleteByUserAndStock(user, stockInWatchlist);
            verify(watchlistRepository).deleteByUserAndStock(user, stockInWatchlist);
        }
    }
    @Nested
    @DisplayName("When user tries to retrieve stocks in watchlist")
    class GetStockFromWatchlist {
        @Test
        @DisplayName("Then do not retrieve stocks if the user does not exist")
        void getStockFromWatchlist_throwsException_whenUserIsNotFound () {
            User user = new User("Bukola", "$2a$10$encodedPassword", Role.USER);
            when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());

            assertThrows(RuntimeException.class, () -> watchlistService.getUserWatchlist(user.getUsername()));

            verify(userRepository).findByUsername(user.getUsername());
        }

        @Test
        @DisplayName("Then retrieve stocks if the user exists")
        void getStockFromWatchlist_succeed_whenUserExists () {
            User user = new User("Bukola", "$2a$10$encodedPassword", Role.USER);
            Stock stockInWatchlist = new Stock(2,	"AAPL", "Apple Inc.", BigDecimal.valueOf(196.98),	BigDecimal.valueOf(1.39),	51334300L, LocalDateTime.of(2025, 4, 21, 13, 53));

            when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
            when(watchlistRepository.findStocksByUser(user)).thenReturn(Collections.singletonList(stockInWatchlist));

            watchlistService.getUserWatchlist(user.getUsername());

            verify(userRepository).findByUsername(user.getUsername());
        }
    }
}
