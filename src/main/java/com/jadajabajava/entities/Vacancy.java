package com.jadajabajava.entities;

import com.jadajabajava.entities.enums.Currency;
import com.jadajabajava.entities.enums.Employment;
import com.jadajabajava.entities.enums.Experience;
import com.jadajabajava.entities.enums.Schedule;
import com.jadajabajava.entities.enums.Type;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.Set;

@Data
@Entity
@Builder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Vacancy extends AbstractEntity {

    @NotNull(message = "{validation.vacancy.remote.id.null}")
    private Long remoteId;

    @NotNull(message = "{validation.vacancy.archived.null}")
    private Boolean archived;

    @NotNull(message = "{validation.vacancy.type.null}")
    private Type type;

    @ManyToOne
    @NotNull(message = "{validation.vacancy.employer.null}")
    private Employer employer;

    @NotBlank(message = "{validation.vacancy.title.blank}")
    private String title;

    @NotBlank(message = "{validation.vacancy.description.blank}")
    private String description;

    @NotNull(message = "{validation.vacancy.area.id.null}")
    private Long areaId;

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

    @NotNull(message = "{validation.vacancy.created.at.null}")
    private OffsetDateTime createdAt;

    @NotNull(message = "{validation.vacancy.published.at.null}")
    private OffsetDateTime publishedAt;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "vacancy_skill",
            joinColumns = @JoinColumn(name = "vacancy_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id"))
    private Set<Skill> skills;
}
