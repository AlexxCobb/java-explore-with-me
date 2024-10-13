package ru.practicum;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.dto.StatRequestDto;
import ru.practicum.dto.StatResponseDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class StatsClient {

    private final RestTemplate rest;
    private final String statUrl;

    public StatsClient(RestTemplate rest, @Value("${client.url}") String statUrl) {
        this.rest = rest;
        this.statUrl = statUrl;
    }

    public void createHitStats(StatRequestDto statRequestDto) {
        HttpEntity<StatRequestDto> requestEntity = new HttpEntity<>(statRequestDto);
        rest.postForEntity(statUrl + "/hit", requestEntity, Void.class);
    }

    public List<StatResponseDto> getStats(String start, String end, List<String> uris, Boolean unique) {

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(statUrl + "/stats")
                .queryParam("start", start).encode(StandardCharsets.UTF_8)
                .queryParam("end", end).encode(StandardCharsets.UTF_8)
                .queryParam("uris", uris)
                .queryParam("unique", unique);

        ResponseEntity<List<StatResponseDto>> rateResponse =
                rest.exchange(builder.toUriString(), HttpMethod.GET,
                        null, new ParameterizedTypeReference<List<StatResponseDto>>() {
                        });
        return rateResponse.getBody();
    }
}