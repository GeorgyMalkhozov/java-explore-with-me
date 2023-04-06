package ru.practicum;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import ru.practicum.client.StatClient;

@SpringBootApplication
public class MainService {

    @Bean
    public StatClient statClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        return new StatClient(serverUrl, builder);
    }

    public static void main(String[] args) {
        SpringApplication.run(MainService.class, args);
    }
}