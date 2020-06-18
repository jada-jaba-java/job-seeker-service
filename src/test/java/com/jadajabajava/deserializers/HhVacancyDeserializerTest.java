package com.jadajabajava.deserializers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.jadajabajava.entities.Vacancy;
import com.jadajabajava.services.EmployerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(MockitoExtension.class)
class HhVacancyDeserializerTest {

    @Mock
    private EmployerService employerService;

    @InjectMocks
    private HhVacancyDeserializer vacancyDeserializer;

    private static final String VACANCY_VALID_JSON_FILE_PATH = "json/vacancy_valid.json";
    private static final String VACANCY_INVALID_JSON_FILE_PATH = "json/vacancy_invalid.json";

    private final ClassLoader classLoader = this.getClass().getClassLoader();

    private ObjectMapper mapper;

    @BeforeEach
    void supplyDeserializer() {
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addDeserializer(Vacancy.class, vacancyDeserializer);
        mapper = new ObjectMapper();
        mapper.registerModule(simpleModule);
    }

    @Test
    void deserialize_validJsonProvided_returnsVacancyObject() {
        try {
            String validJson = new String(Objects.requireNonNull(classLoader.getResourceAsStream(VACANCY_VALID_JSON_FILE_PATH)).readAllBytes());

            assertDoesNotThrow(() -> mapper.readValue(validJson, Vacancy.class));
        } catch (IOException e) {
            fail("Unexpected exception: " + e.getMessage(), e);
        }
    }

    @Test
    void deserialize_invalidJsonProvided_throwsException() {
        try {
            String invalidJson = new String(Objects.requireNonNull(classLoader.getResourceAsStream(VACANCY_INVALID_JSON_FILE_PATH)).readAllBytes());

            assertThrows(JsonProcessingException.class, () -> mapper.readValue(invalidJson, Vacancy.class));
        } catch (IOException e) {
            fail("Unexpected exception: " + e.getMessage(), e);
        }
    }
}
