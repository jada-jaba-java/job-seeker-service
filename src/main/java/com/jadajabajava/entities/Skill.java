package com.jadajabajava.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class Skill extends AbstractEntity {

    @NotBlank(message = "{validation.skill.title.blank}")
    private String title;

    public Skill(String title) {
        this.title = title;
    }
}
