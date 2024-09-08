package ru.practicum;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestTemplate;
import ru.practicum.DTO.StatRequestDto;

@SpringBootApplication
public class MainServiceApp {
    public static void main(String[] args) {
        StatsClient statsClient = new StatsClient(new RestTemplate(), "http://localhost:9090");
        statsClient.createHitStats(new StatRequestDto("ewm-main-service", "/events", "121.0.0.1", "2024-09-08 13:58:08"));
    }
}
