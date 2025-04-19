package org.bukola.stockmarket.dto.alphavantage;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class GlobalQuote {
    @JsonProperty("01. symbol") private String symbol;
    @JsonProperty("05. price") private BigDecimal price;
    @JsonProperty("09. change") private BigDecimal changeAbsolute;
    @JsonProperty("10. change percent") private String changePercent;

    @JsonProperty("06. volume") private Long volume;
    @JsonProperty("07. latest trading day") private LocalDate lastTradingDay;
}
