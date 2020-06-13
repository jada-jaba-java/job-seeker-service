package com.jadajabajava.providers;

import com.jadajabajava.entities.Vacancy;

import javax.management.Query;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Set;

public class HhVacancyProvider implements VacancyProvider {

    public static final String HH_API_URL = "https://api.hh.ru";

    public static final String AREA_ID = "";
    public static final String QUERY = "NAME:java AND NOT android";

    @Override
    public Set<Vacancy> fetchVacancies() {
        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        String encodedQuery = URLEncoder.encode(QUERY, StandardCharsets.UTF_8);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(HH_API_URL + "/vacancies"))
                .setHeader("User-Agent", "JJS")
                .timeout(Duration.ofSeconds(10))
                .build();
        try {
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
