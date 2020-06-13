package com.jadajabajava.entities;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Data
@Entity
public class Employer extends AbstractEntity {

    @NotBlank(message = "{validation.vacancy.employer.title.blank}")
    private String title;

    @OneToMany(mappedBy = "employer")
    private Set<Vacancy> vacancies;
}
