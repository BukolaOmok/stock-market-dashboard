package org.bukola.stockmarket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class StockMarketDashboardApplication {

	public static void main(String[] args) {
		SpringApplication.run(StockMarketDashboardApplication.class, args);
	}

}
