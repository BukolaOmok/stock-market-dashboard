package org.bukola.stockmarket.configuration;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

@Slf4j
public class TwelveDataErrorHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().isError();
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        log.error("Twelve Data API error: {} - {}",
                response.getStatusCode(),
                new String(response.getBody().readAllBytes()));

        if (response.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
            throw new RuntimeException("Rate limit exceeded");
        }
    }
}
