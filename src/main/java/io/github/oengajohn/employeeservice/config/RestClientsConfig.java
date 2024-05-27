package io.github.oengajohn.employeeservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class RestClientsConfig {

    @Bean
    WebClient webClient() {
        return WebClient.builder().build();
    }

    @Bean
    RestClient restClient() {
        return RestClient.builder().build();
    }

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
