package com.jadajabajava.services;

import com.jadajabajava.entities.Vacancy;
import com.jadajabajava.exceptions.CommonServiceException;
import com.jadajabajava.exceptions.VacancyProviderException;
import com.jadajabajava.providers.VacancyProvider;
import com.jadajabajava.repositories.VacancyRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@Transactional
public class VacancyService {

    private final VacancyRepository repository;

    private final VacancyProvider vacancyProvider;

    private final MessageSource messageSource;

    @Autowired
    public VacancyService(VacancyRepository repository, VacancyProvider vacancyProvider, MessageSource messageSource) {
        this.repository = repository;
        this.vacancyProvider = vacancyProvider;
        this.messageSource = messageSource;
    }

    public void refreshVacancies(@NonNull String query, @NonNull List<Long> areas) throws CommonServiceException {
        try {
            Set<Vacancy> vacancies = vacancyProvider.requestVacancies(query, areas);
            vacancies.forEach(repository::save);
        } catch (VacancyProviderException e) {
            String exceptionMessage = messageSource.getMessage(
                    "exception.service.vacancy.refresh.failed", new Object[] { e.getMessage() }, LocaleContextHolder.getLocale());
            log.debug(exceptionMessage, e);
            throw new CommonServiceException(exceptionMessage, e);
        }
    }
}
