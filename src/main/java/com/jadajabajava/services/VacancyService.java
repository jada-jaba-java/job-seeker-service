package com.jadajabajava.services;

import com.jadajabajava.entities.Vacancy;
import com.jadajabajava.repositories.VacancyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class VacancyService extends AbstractService<Vacancy> {

    @Autowired
    public VacancyService(VacancyRepository repository) {
        this.repository = repository;
    }

    public void refreshVacancies() {
        // request HH server with query
        // receive response
        // map response to vacancy object


    }
}
