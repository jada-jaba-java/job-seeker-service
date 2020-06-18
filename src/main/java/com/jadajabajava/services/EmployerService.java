package com.jadajabajava.services;

import com.jadajabajava.entities.Employer;
import com.jadajabajava.repositories.EmployerRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class EmployerService {

    private final EmployerRepository repository;

    private final MessageSource messageSource;

    @Autowired
    public EmployerService(EmployerRepository repository, MessageSource messageSource) {
        this.repository = repository;
        this.messageSource = messageSource;
    }

    public Optional<Employer> findByRemoteId(@NonNull Long remoteId) {
        return repository.findByRemoteId(remoteId);
    }
}
