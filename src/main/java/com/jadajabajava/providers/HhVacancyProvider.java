package com.jadajabajava.providers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.jadajabajava.deserializers.HhVacancyDeserializer;
import com.jadajabajava.entities.Vacancy;
import com.jadajabajava.exceptions.VacancyProviderException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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

    public static final String USER_AGENT = "JSS";

    private final HttpClient httpClient;
    private final ObjectMapper mapper;
    private final MessageSource messageSource;

    @Autowired
    public HhVacancyProvider(HhVacancyDeserializer vacancyDeserializer, MessageSource messageSource, HttpClient httpClient) {
        this.messageSource = messageSource;
        this.httpClient = httpClient;

        SimpleModule module = new SimpleModule();
        module.addDeserializer(Vacancy.class, vacancyDeserializer);
        mapper = new ObjectMapper();
        mapper.registerModule(module);
    }

    @Override
    public Set<Vacancy> requestVacancies(String query, List<Long> areas) throws VacancyProviderException {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromUriString(HH_API_URL)
                .path(HH_VACANCIES_PATH)
                .queryParam("text", query)
                .queryParam("order_by", HH_ORDER_BY)
                .queryParam("period", HH_PERIOD_DAYS)
                .queryParam("per_page", HH_ITEMS_PER_PAGE)
                .queryParam("no_magic ", HH_DISABLE_MAGIC);

        // multiple areas in one query via HH-specific "area" params handling
        areas.forEach(areaId -> uriBuilder.queryParam("area", areaId));

        Set<Vacancy> vacancies = new HashSet<>();
        try {
            Set<Long> vacancyIdList = fetchVacancyIds(HH_START_PAGE_NUMBER, uriBuilder, httpClient);
            log.debug("Total fetched vacancy IDs: {}", vacancyIdList.size());
            for (Long vacancyId : vacancyIdList) {
                vacancies.add(requestVacancyById(vacancyId));
            }
        } catch (IOException e) {
            String exceptionMessage = messageSource.getMessage("exception.provider.vacancy.request.failed", new Object[]{}, LocaleContextHolder.getLocale());
            log.debug(exceptionMessage, e);
            throw new VacancyProviderException(exceptionMessage, e);
        } catch (InterruptedException e) {
            log.debug("Thread was interrupted. Further execution is unsafe", e);
            Thread.currentThread().interrupt();
        }
        return vacancies;
    }

    private Vacancy requestVacancyById(Long hhVacancyId) throws IOException, InterruptedException {
        UriComponents uriComponents = UriComponentsBuilder
                .fromUriString(HH_API_URL)
                .path(HH_VACANCIES_PATH + hhVacancyId)
                .encode()
                .build();

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(uriComponents.toUri())
                .setHeader("User-Agent", USER_AGENT)
                .build();

        log.debug("Executing HTTP request: {}", uriComponents.toUri());
        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        log.debug("Got response with code {} (body length: {})", httpResponse.statusCode(), httpResponse.body().length());
        return mapper.readValue(httpResponse.body(), Vacancy.class);
    }

    private Set<Long> fetchVacancyIds(Integer startPageNumber, UriComponentsBuilder uriBuilder, HttpClient httpClient) throws IOException, InterruptedException {
        UriComponents uriComponents = uriBuilder
                .cloneBuilder()
                .queryParam("page", startPageNumber)
                .encode()
                .build();

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(uriComponents.toUri())
                .setHeader("User-Agent", USER_AGENT)
                .build();

        log.debug("Executing HTTP request: {}", uriComponents.toUri());
        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        log.debug("Got response with code {} (body length: {})", httpResponse.statusCode(), httpResponse.body().length());

        JsonNode root = mapper.readTree(httpResponse.body());
        Set<Long> vacancyIdList = fetchVacancyIdsFromNode(root);
        int totalPages = root.get("pages").asInt();
        int nextPage = startPageNumber + 1;
        if (nextPage < totalPages) {
            vacancyIdList.addAll(fetchVacancyIds(nextPage, uriBuilder, httpClient));
        }
        return vacancyIdList;
    }

    private Set<Long> fetchVacancyIdsFromNode(JsonNode rootNode) {
        JsonNode vacanciesNode = rootNode.get("items");
        Iterator<JsonNode> vacancyIterator = vacanciesNode.elements();
        Set<Long> vacancyIdList = new HashSet<>();

        if (!vacancyIterator.hasNext()) {
            log.debug("Response contains empty items list");
            return vacancyIdList;
        }

        while (vacancyIterator.hasNext()) {
            Long vacancyId = vacancyIterator.next().get("id").asLong();
            vacancyIdList.add(vacancyId);
        }
        log.debug("Fetched {} vacancies from page #{}", vacancyIdList.size(), rootNode.get("page").asText());
        return vacancyIdList;
    }
}
