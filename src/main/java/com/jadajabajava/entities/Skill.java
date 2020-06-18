package com.jadajabajava.entities;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotBlank;

@Data
@Entity
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "skill_seq_gen")
    @SequenceGenerator(name = "skill_seq_gen", sequenceName = "skill_sequence")
    private Long id;

    @NotBlank(message = "{validation.skill.title.blank}")
    private String title;

    public Skill(String title) {
        this.title = title;
    }
}
