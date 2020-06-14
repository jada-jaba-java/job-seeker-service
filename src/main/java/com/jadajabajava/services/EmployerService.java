package com.jadajabajava.services;

import com.jadajabajava.entities.Employer;
import com.jadajabajava.repositories.EmployerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class EmployerService extends AbstractService<Employer> {

    private final EmployerRepository employerRepository;

    private final MessageSource messageSource;

    @Autowired
    public EmployerService(EmployerRepository repository, MessageSource messageSource) {
        this.repository = repository;
        this.employerRepository = repository;
        this.messageSource = messageSource;
    }

    public Optional<Employer> findByRemoteId(Long remoteId) {
        return employerRepository.findByRemoteId(remoteId);
    }
}
