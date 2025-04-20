package org.bukola.stockmarket.dto.twelvedata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TwelveDataResponse {
    private String symbol;
    private String name;
    private String currency;

    @JsonProperty("close")
    private String price;

    @JsonProperty("percent_change")
    private String changePercent;

    private String volume;
    private String market_cap;
    private String sector;
}
