package com.jadajabajava.providers;

import com.jadajabajava.entities.Vacancy;

import java.util.Set;

public interface VacancyProvider {

    Set<Vacancy> fetchVacancies();
}
