package com.jadajabajava.services;

import com.jadajabajava.entities.Vacancy;
import com.jadajabajava.exceptions.CommonServiceException;
import com.jadajabajava.exceptions.VacancyProviderException;
import com.jadajabajava.providers.VacancyProvider;
import com.jadajabajava.repositories.VacancyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VacancyServiceTest {

    @Mock
    private VacancyRepository vacancyRepository;

    @Mock
    private VacancyProvider vacancyProvider;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private VacancyService vacancyService;

    @Mock
    private Vacancy mockedVacancyOne;

    @Mock
    private Vacancy mockedVacancyTwo;

    @Test
    void refreshVacancies_vacancyRequestFailed_throwsException() {
        String query = "algol";
        List<Long> areas = List.of(2019L);
        String exceptionTest = "Unable to complete vacancies request";

        try {
            when(vacancyProvider.requestVacancies(query, areas))
                    .thenThrow(new VacancyProviderException(exceptionTest));

            assertThrows(CommonServiceException.class,
                    () -> vacancyService.refreshVacancies(query, areas));

            verify(vacancyProvider).requestVacancies(query, areas);
        } catch (VacancyProviderException e) {
            fail("Unexpected exception: " + e.getMessage(), e);
        }
    }

    @Test
    void refreshVacancies_vacancyRequestSuccessful_vacanciesSaved() {
        String query = "java";
        List<Long> areas = List.of(2L);
        Set<Vacancy> mockedVacancies = Set.of(mockedVacancyOne, mockedVacancyTwo);

        try {
            when(vacancyProvider.requestVacancies(query, areas))
                    .thenReturn(mockedVacancies);

            assertDoesNotThrow(() -> vacancyService.refreshVacancies(query, areas));

            verify(vacancyProvider).requestVacancies(query,areas);
            verify(vacancyRepository).save(mockedVacancyOne);
            verify(vacancyRepository).save(mockedVacancyTwo);
        } catch (VacancyProviderException e) {
            fail("Unexpected exception: " + e.getMessage(), e);
        }
    }
}
