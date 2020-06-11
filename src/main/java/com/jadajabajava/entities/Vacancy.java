package com.jadajabajava.entities;

import com.jadajabajava.entities.enums.Currency;
import com.jadajabajava.entities.enums.Employment;
import com.jadajabajava.entities.enums.Experience;
import com.jadajabajava.entities.enums.Schedule;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import java.util.Set;

@Data
@Entity
public class Vacancy extends AbstractEntity {

    private String title;
    private String description;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "vacancy_keyword",
            joinColumns = @JoinColumn(name = "vacancy_id"),
            inverseJoinColumns = @JoinColumn(name = "keyword_id"))
    private Set<Keyword> keywords;

    private String regionId;

    private long minSalary;
    private long maxSalary;
    private Currency currency;

    private Employment employment;
    private Experience experience;
    private Schedule schedule;
}
