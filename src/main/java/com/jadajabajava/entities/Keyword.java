package com.jadajabajava.entities;

import lombok.Data;

import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;

@Data
@Entity
public class Keyword extends AbstractEntity {

    @NotBlank(message = "{validation.keyword.word.blank}")
    private String word;
}
