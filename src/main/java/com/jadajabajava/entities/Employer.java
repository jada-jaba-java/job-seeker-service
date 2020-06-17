package com.jadajabajava.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Employer {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "employer_seq_gen")
    @SequenceGenerator(name = "employer_seq_gen", sequenceName = "employer_sequence")
    private Long id;

    @NotBlank(message = "{validation.employer.title.blank}")
    private String title;

    @NotNull(message = "{validation.employer.remote.id.null}")
    private Long remoteId;

    @OneToMany(mappedBy = "employer", orphanRemoval = true)
    private Set<Vacancy> vacancies;
}
