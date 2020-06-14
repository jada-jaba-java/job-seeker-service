package com.jadajabajava.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@Entity
@Builder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Employer extends AbstractEntity {

    @NotBlank(message = "{validation.employer.title.blank}")
    private String title;

    @NotNull(message = "{validation.employer.remote.id.null}")
    private Long remoteId;

    @OneToMany(mappedBy = "employer")
    private Set<Vacancy> vacancies;
}
