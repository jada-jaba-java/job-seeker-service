package com.jadajabajava.services;

import com.jadajabajava.entities.Employer;
import com.jadajabajava.repositories.EmployerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class EmployerService {

    private final EmployerRepository repository;

    @Autowired
    public EmployerService(EmployerRepository repository) {
        this.repository = repository;
    }

    public Optional<Employer> findByRemoteId(Long remoteId) {
        return repository.findByRemoteId(remoteId);
    }
}
