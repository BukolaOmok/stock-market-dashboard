package org.bukola.stockmarket.dto.alphavantage;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AlphaVantageResponse {
    @JsonProperty("Global Quote")
    private GlobalQuote globalQuote;
}
