package com.jadajabajava.providers;

import com.jadajabajava.entities.Vacancy;
import com.jadajabajava.exceptions.VacancyProviderException;
import java.util.List;
import java.util.Set;

public interface VacancyProvider {

    Set<Vacancy> requestVacancies(String query, List<Long> areas) throws VacancyProviderException;

}
