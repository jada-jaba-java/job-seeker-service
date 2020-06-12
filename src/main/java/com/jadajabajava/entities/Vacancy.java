package com.jadajabajava.entities;

import com.jadajabajava.entities.enums.Currency;
import com.jadajabajava.entities.enums.Employment;
import com.jadajabajava.entities.enums.Experience;
import com.jadajabajava.entities.enums.Schedule;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@Entity
public class Vacancy extends AbstractEntity {

    @NotBlank(message = "{validation.vacancy.title.blank}")
    private String title;

    @NotBlank(message = "{validation.vacancy.description.blank}")
    private String description;

    @Range(min = 1, max = 5000, message = "{validation.vacancy.region.range}")
    private Integer regionId;

    @Range(min = 0, max = 2000000, message = "{validation.vacancy.min.salary.range}")
    private Integer minSalary;

    @Range(min = 0, max = 2000000, message = "{validation.vacancy.max.salary.range}")
    private Integer maxSalary;

    @NotNull(message = "{validation.vacancy.currency.null}")
    private Currency currency;

    @NotNull(message = "{validation.vacancy.employment.null}")
    private Employment employment;

    @NotNull(message = "{validation.vacancy.experience.null}")
    private Experience experience;

    @NotNull(message = "{validation.vacancy.schedule.null}")
    private Schedule schedule;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "vacancy_keyword",
            joinColumns = @JoinColumn(name = "vacancy_id"),
            inverseJoinColumns = @JoinColumn(name = "keyword_id"))
    private Set<Keyword> keywords;
}
