package ru.practicum;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.DTO.StatRequestDto;
import ru.practicum.DTO.StatResponseDto;

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

    public StatResponseDto getStats(String start, String end, List<String> uris, Boolean unique) {

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(statUrl + "/stats")
                .queryParam("start", start).encode(StandardCharsets.UTF_8)
                .queryParam("end", end).encode(StandardCharsets.UTF_8)
                .queryParam("uris", uris)
                .queryParam("unique", unique);
        return rest.getForObject(builder.toUriString(), StatResponseDto.class);
    }
}
