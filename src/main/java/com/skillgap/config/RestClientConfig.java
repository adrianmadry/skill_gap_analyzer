package com.skillgap.config;

import java.net.http.HttpClient;
import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.http.HttpHeaders;


@Configuration
public class RestClientConfig {

    @Bean    
    public RestClient.Builder commonRestClientBuilder() {
        HttpClient httpClient = HttpClient.newBuilder()
                                    .connectTimeout(Duration.ofSeconds(10))
                                    .followRedirects(HttpClient.Redirect.NORMAL)
                                    .build();
        
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(Duration.ofSeconds(10));

        return RestClient.builder()
                        .requestFactory(requestFactory)
                        .defaultHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36")
                        .defaultHeader(HttpHeaders.ACCEPT, "application/json, text/plain, */*")
                        .defaultHeader(HttpHeaders.ACCEPT_LANGUAGE, "pl-PL,pl;q=0.9,en-US;q=0.8,en;q=0.7");
    }   

    @Bean
    public RestClient justJoinItRestClient(RestClient.Builder commonRestClientBuilder) {
        return commonRestClientBuilder
                    .baseUrl("https://justjoin.it/api/candidate-api/offers")
                    .build();
    }
}
