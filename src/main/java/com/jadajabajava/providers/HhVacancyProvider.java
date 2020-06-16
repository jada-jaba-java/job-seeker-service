package com.jadajabajava.providers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.jadajabajava.deserializers.HhVacancyDeserializer;
import com.jadajabajava.entities.Vacancy;
import com.jadajabajava.exceptions.HttpRequestException;
import com.jadajabajava.services.VacancyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Component
public class HhVacancyProvider implements VacancyProvider {

    public static final String HH_API_URL = "https://api.hh.ru";
    public static final String HH_VACANCIES_PATH = "/vacancies/";
    public static final String HH_ORDER_BY = "publication_time";
    public static final Integer HH_ITEMS_PER_PAGE = 8; // max 100
    public static final Integer HH_PERIOD_DAYS = 30; // max 30
    public static final Integer HH_START_PAGE_NUMBER = 0;

    // turn off auto detection of region, salary etc from query string
    public static final Boolean HH_DISABLE_MAGIC = true;

    public static final String QUERY = "java";
    public static final List<Integer> AREA_IDS = List.of(2019);

    public static final String USER_AGENT = "JSS";
    public static final Integer DEFAULT_TIMEOUT_SEC = 10;

    private final HttpClient httpClient;

    private final VacancyService vacancyService;

    private final HhVacancyDeserializer vacancyDeserializer;

    @Autowired
    public HhVacancyProvider(VacancyService vacancyService, HhVacancyDeserializer vacancyDeserializer) {
        this.vacancyService = vacancyService;
        this.vacancyDeserializer = vacancyDeserializer;

        httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(DEFAULT_TIMEOUT_SEC))
                .build();
    }

    @Override
    public Set<Vacancy> fetchVacancies() {
        try {
            Set<Long> vacancyIds = fetchVacancyIds();

            Vacancy vacancy = fetchVacancyData(vacancyIds.iterator().next());

            vacancyService.save(vacancy);


        } catch (HttpRequestException e) {
            log.error("Unable to fetch vacancies: {}", e.getMessage(), e);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new HashSet<>();
    }

    private Set<Long> fetchVacancyIds() throws HttpRequestException {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromUriString(HH_API_URL)
                .path(HH_VACANCIES_PATH)
                .queryParam("text", QUERY)
                .queryParam("order_by", HH_ORDER_BY)
                .queryParam("period", HH_PERIOD_DAYS)
                .queryParam("per_page", HH_ITEMS_PER_PAGE)
                .queryParam("no_magic ", HH_DISABLE_MAGIC);

        // multiple areas in one query via HH-specific "area" params handling
        AREA_IDS.forEach(areaId -> uriBuilder.queryParam("area", areaId));

        try {
            Set<Long> vacancyIdList = fetchLinksFromPage(HH_START_PAGE_NUMBER, uriBuilder, httpClient);
            log.debug("Total fetched vacancy IDs: {}", vacancyIdList.size());
            return vacancyIdList;
        } catch (IOException | InterruptedException e) {
            log.error("HTTP request failed: {}", e.getMessage(), e);
            throw new HttpRequestException(e.getMessage(), e);
        }
    }

    private Vacancy fetchVacancyData(Long hhVacancyId) throws IOException, InterruptedException {
        UriComponents uriComponents = UriComponentsBuilder
                .fromUriString(HH_API_URL)
                .path(HH_VACANCIES_PATH + hhVacancyId)
                .encode()
                .build();

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(uriComponents.toUri())
                .setHeader("User-Agent", USER_AGENT)
                .timeout(Duration.ofSeconds(DEFAULT_TIMEOUT_SEC))
                .build();

        log.debug("Executing HTTP request: {}", uriComponents.toUri());

        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        log.debug("Got response with code {}. Body length: {}", httpResponse.statusCode(), httpResponse.body().length());

        log.debug(httpResponse.body());

        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Vacancy.class, vacancyDeserializer);
        mapper.registerModule(module);

        return mapper.readValue(httpResponse.body(), Vacancy.class);
    }

    private Set<Long> fetchLinksFromPage(Integer pageNumber, UriComponentsBuilder uriBuilder, HttpClient httpClient) throws IOException, InterruptedException {
        UriComponents uriComponents = uriBuilder
                .cloneBuilder()
                .queryParam("page", pageNumber)
                .encode()
                .build();

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(uriComponents.toUri())
                .setHeader("User-Agent", USER_AGENT)
                .timeout(Duration.ofSeconds(DEFAULT_TIMEOUT_SEC))
                .build();

        log.debug("Executing HTTP request: {}", uriComponents.toUri());

        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        log.debug("Got response with code {}. Body length: {}", httpResponse.statusCode(), httpResponse.body().length());

        String jsonBody = httpResponse.body();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(jsonBody);
        JsonNode vacanciesNode = root.get("items");

        if (Objects.isNull(vacanciesNode)) {
            log.error("Invalid response: {}", jsonBody);
            // no such item (invalid response)
        }

        Set<Long> vacancyIdList = new HashSet<>();
        Iterator<JsonNode> vacancyIterator = vacanciesNode.elements();

        if (!vacancyIterator.hasNext()) {
            log.debug("Response contains empty items list");
            return vacancyIdList;
        }

        while (vacancyIterator.hasNext()) {
            Long vacancyId = vacancyIterator.next().get("id").asLong();
            vacancyIdList.add(vacancyId);
        }
        log.debug("Fetched {} vacancies from page #{}", vacancyIdList.size(), pageNumber);

        Integer totalPages = root.get("pages").asInt();
        if (++pageNumber < totalPages) {
            Thread.sleep(100); // sleep between requests to prevent ban
            vacancyIdList.addAll(fetchLinksFromPage(pageNumber, uriBuilder, httpClient));
        }

        return vacancyIdList;
    }
}
