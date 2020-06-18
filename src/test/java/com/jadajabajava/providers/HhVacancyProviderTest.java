package com.jadajabajava.providers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jadajabajava.deserializers.HhVacancyDeserializer;
import com.jadajabajava.entities.Vacancy;
import com.jadajabajava.exceptions.VacancyProviderException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HhVacancyProviderTest {

    @Mock
    private HttpClient httpClient;

    @Mock
    private HttpResponse<String> httpResponseList;

    @Mock
    private HttpResponse<String> httpResponseVacancy;

    @Mock
    private ObjectMapper mapper;

    @Mock
    private MessageSource messageSource;

    @Mock
    private HhVacancyDeserializer vacancyDeserializer;

    @InjectMocks
    private HhVacancyProvider vacancyProvider;

    @Captor
    private ArgumentCaptor<HttpRequest> requestCaptor;

    private static final String VACANCIES_LIST_VALID_JSON_FILE_PATH = "json/vacancies_list_valid.json";
    private static final String VACANCIES_LIST_INVALID_JSON_FILE_PATH = "json/vacancies_list_invalid.json";
    private static final String VACANCIES_LIST_EMPTY_JSON_FILE_PATH = "json/vacancies_list_empty.json";
    private static final String VACANCY_VALID_JSON_FILE_PATH = "json/vacancy_valid.json";

    private final ClassLoader classLoader = this.getClass().getClassLoader();

    // handle invalid response
    // handle valid response

    @Test
    void requestVacancies_buildsRequestCorrectly() {
        String query = "queryThatProducesEmptyResponse";
        Long area = 2019L;

        try {
            String validJson = new String(Objects.requireNonNull(classLoader.getResourceAsStream(VACANCIES_LIST_EMPTY_JSON_FILE_PATH)).readAllBytes());

            when(httpResponseList.statusCode()).thenReturn(HttpStatus.OK.value());
            when(httpResponseList.body()).thenReturn(validJson);
            when(httpClient.send(any(), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(httpResponseList);

            vacancyProvider.requestVacancies(query, List.of(area));

            verify(httpClient).send(requestCaptor.capture(), any());
        } catch (VacancyProviderException | IOException | InterruptedException e) {
            fail("Unexpected exception: " + e.getMessage(), e);
        }

        String capturedRequestQuery = requestCaptor.getValue().uri().getQuery();

        assertTrue(capturedRequestQuery.contains(query));
        assertTrue(capturedRequestQuery.contains(String.valueOf(area)));
    }

    @Test
    void requestVacancies_invalidResponse_handlesCorrectly() {
        String query = "someTextThatLeadsToInvalidResponse";
        Long area = 99999L;

        try {
            String invalidJson = new String(Objects.requireNonNull(classLoader.getResourceAsStream(VACANCIES_LIST_INVALID_JSON_FILE_PATH)).readAllBytes());

            when(httpResponseList.statusCode()).thenReturn(HttpStatus.OK.value());
            when(httpResponseList.body()).thenReturn(invalidJson);
            when(httpClient.send(any(), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(httpResponseList);

            assertThrows(VacancyProviderException.class,
                    () -> vacancyProvider.requestVacancies(query, List.of(area)));

            verify(httpClient).send(any(), any());
        } catch (IOException | InterruptedException e) {
            fail("Unexpected exception: " + e.getMessage(), e);
        }
    }

    @Test
    void requestVacancies_validResponse_returnsExpectedData() {
        String query = "java";
        Long area = 145L;
        Long expectedVacancyRemoteId = 37504692L;
        String expectedVacancyTitle = "Java Developer";
        Integer expectedSize = 1;

        Vacancy expectedVacancy = Vacancy.builder()
                .remoteId(expectedVacancyRemoteId)
                .title(expectedVacancyTitle)
                .build();
        try {
            String validListJson = new String(Objects.requireNonNull(classLoader.getResourceAsStream(VACANCIES_LIST_VALID_JSON_FILE_PATH)).readAllBytes());
            String validVacancyJson = new String(Objects.requireNonNull(classLoader.getResourceAsStream(VACANCY_VALID_JSON_FILE_PATH)).readAllBytes());

            // query for vacancy list
            when(httpResponseList.statusCode()).thenReturn(HttpStatus.OK.value());
            when(httpResponseList.body()).thenReturn(validListJson);
            when(httpClient.send(argThat(req -> {
                if (req == null) return false;
                String httpQuery = req.uri().getQuery();
                return httpQuery.contains(query) && httpQuery.contains(area.toString());
            }), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(httpResponseList);

            // query for vacancy
            when(httpResponseVacancy.statusCode()).thenReturn(HttpStatus.OK.value());
            when(httpResponseVacancy.body()).thenReturn(validVacancyJson);
            when(httpClient.send(argThat(req -> {
                if (req == null) return false;
                return req.uri().getPath().contains(String.valueOf(expectedVacancyRemoteId));
            }), eq(HttpResponse.BodyHandlers.ofString())))
                    .thenReturn(httpResponseVacancy);
            when(vacancyDeserializer.deserialize(any(), any()))
                    .thenReturn(expectedVacancy);

            Set<Vacancy> vacancies = vacancyProvider.requestVacancies(query, List.of(area));
            assertEquals(expectedSize, vacancies.size());

            Vacancy actualVacancy = vacancies.iterator().next();
            assertEquals(expectedVacancyTitle, actualVacancy.getTitle());
        } catch (VacancyProviderException | IOException | InterruptedException e) {
            fail("Unexpected exception: " + e.getMessage(), e);
        }
    }
}
