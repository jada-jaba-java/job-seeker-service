package com.jadajabajava.services;

import com.jadajabajava.entities.Vacancy;
import com.jadajabajava.exceptions.VacancyProviderException;
import com.jadajabajava.providers.VacancyProvider;
import com.jadajabajava.repositories.VacancyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public VacancyService(VacancyRepository repository, VacancyProvider vacancyProvider) {
        this.repository = repository;
        this.vacancyProvider = vacancyProvider;
    }

    public void refreshVacancies(String query, List<Integer> areas) {
        try {
            Set<Vacancy> vacancies = vacancyProvider.requestVacancies(query, areas);
            vacancies.forEach(vacancy -> repository.save(vacancy));
        } catch (VacancyProviderException e) {
            log.debug("Failed to refresh vacancies: {}", e.getMessage(), e);
            // TODO: we have to throw something here
        }
    }
}
