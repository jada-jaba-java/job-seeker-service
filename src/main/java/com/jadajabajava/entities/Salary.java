package com.jadajabajava.entities;

import com.jadajabajava.entities.enums.Currency;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Embeddable
@NoArgsConstructor
public class Salary {

    @Range(min = 0, max = 2000000, message = "{validation.salary.min.range}")
    private Integer minSalary;

    @Range(min = 0, max = 2000000, message = "{validation.salary.max.range}")
    private Integer maxSalary;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    public Salary(Integer minSalary, Integer maxSalary, Currency currency) {
        this.minSalary = minSalary;
        this.maxSalary = maxSalary;
        this.currency = currency;
    }
}
