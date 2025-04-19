package org.bukola.stockmarket.dto.alphavantage;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OverviewResponse {
    @JsonProperty("Symbol")
    private String symbol;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Sector")
    private String sector;

    @JsonProperty("Industry")
    private String industry;

    @JsonProperty("MarketCapitalization")
    private String marketCap;

    @JsonProperty("PERatio")
    private String peRatio;    

    @JsonProperty("Description")
    private String description;
}