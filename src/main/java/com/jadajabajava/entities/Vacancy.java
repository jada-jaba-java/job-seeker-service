package com.jadajabajava.entities;

import com.jadajabajava.entities.enums.Employment;
import com.jadajabajava.entities.enums.Experience;
import com.jadajabajava.entities.enums.Schedule;
import com.jadajabajava.entities.enums.Type;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.Set;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vacancy {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vacancy_seq_gen")
    @SequenceGenerator(name = "vacancy_seq_gen", sequenceName = "vacancy_sequence")
    private Long id;

    @NotNull(message = "{validation.vacancy.remote.id.null}")
    private Long remoteId;

    @NotNull(message = "{validation.vacancy.archived.null}")
    private Boolean archived;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "{validation.vacancy.type.null}")
    private Type type;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @NotNull(message = "{validation.vacancy.employer.null}")
    private Employer employer;

    @NotBlank(message = "{validation.vacancy.title.blank}")
    private String title;

    @NotBlank(message = "{validation.vacancy.description.blank}")
    private String description;

    @NotNull(message = "{validation.vacancy.area.id.null}")
    private Long areaId;

    @NotNull(message = "{validation.vacancy.salary.null}")
    private Salary salary;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "{validation.vacancy.employment.null}")
    private Employment employment;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "{validation.vacancy.experience.null}")
    private Experience experience;

    @Enumerated(EnumType.STRING)
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
