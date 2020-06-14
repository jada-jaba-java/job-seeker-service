package com.jadajabajava.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.jadajabajava.entities.Employer;
import com.jadajabajava.entities.Skill;
import com.jadajabajava.entities.Vacancy;
import com.jadajabajava.entities.enums.Currency;
import com.jadajabajava.entities.enums.Employment;
import com.jadajabajava.entities.enums.Experience;
import com.jadajabajava.entities.enums.Schedule;
import com.jadajabajava.entities.enums.Type;
import com.jadajabajava.services.EmployerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Component
public class HhVacancyDeserializer extends JsonDeserializer<Vacancy> {
    private static final Float NET_RATIO = 0.87F;
    private static final String HH_DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ssZ";

    private final EmployerService employerService;

    @Autowired
    public HhVacancyDeserializer(EmployerService employerService) {
        this.employerService = employerService;
    }

    @Override
    public Vacancy deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode root = jsonParser.getCodec().readTree(jsonParser);
        Long remoteId = root.get("id").asLong();
        Boolean isArchived = root.get("archived").asBoolean(false);
        Type type = Type.valueOf(root.get("type").get("id").asText().toUpperCase());
        Long employerRemoteId = root.get("employer").get("id").asLong();
        String employerTitle = root.get("employer").get("name").asText();
        String vacancyTitle = root.get("name").asText();
        String description = root.get("description").asText();
        Long areaId = root.get("area").get("id").asLong();

        int salaryFrom = root.get("salary").get("from").asInt(0);
        int salaryTo = root.get("salary").get("to").asInt(0);
        if (root.get("salary").get("gross").asBoolean()) {
            salaryFrom = Math.round(salaryFrom * NET_RATIO);
            salaryTo = Math.round(salaryTo * NET_RATIO);
        }
        Currency currency = Currency.valueOf(root.get("salary").get("currency").asText().toUpperCase());
        Employment employment = Employment.valueOf(root.get("employment").get("id").asText().toUpperCase());
        Experience experience = Experience.valueOf(root.get("experience").get("id").asText().toUpperCase());
        Schedule schedule = Schedule.valueOf(root.get("schedule").get("id").asText().toUpperCase());

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(HH_DATE_TIME_PATTERN);

        OffsetDateTime createdAt = OffsetDateTime.parse(root.get("created_at").asText(), dateTimeFormatter);
        OffsetDateTime publishedAt = OffsetDateTime.parse(root.get("published_at").asText(), dateTimeFormatter);

        Iterator<JsonNode> keySkills = root.get("key_skills").elements();
        Set<Skill> vacancySkills = new HashSet<>();
        while (keySkills.hasNext()) {
            String skillTitle = keySkills.next().get("name").asText();
            vacancySkills.add(new Skill(skillTitle));
        }

        Employer employer = employerService.findByRemoteId(employerRemoteId).orElseGet(() ->
                Employer.builder()
                        .remoteId(employerRemoteId)
                        .title(employerTitle)
                        .build()
        );

        employerService.save(employer);

        return Vacancy.builder()
                .remoteId(remoteId)
                .archived(isArchived)
                .type(type)
                .employer(employer)
                .title(vacancyTitle)
                .description(description)
                .areaId(areaId)
                .minSalary(salaryFrom)
                .maxSalary(salaryTo)
                .currency(currency)
                .employment(employment)
                .experience(experience)
                .schedule(schedule)
                .createdAt(createdAt)
                .publishedAt(publishedAt)
                .skills(vacancySkills)
                .build();
    }
}
