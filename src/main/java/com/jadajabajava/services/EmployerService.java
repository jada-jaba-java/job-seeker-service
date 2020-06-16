package com.jadajabajava.services;

import com.jadajabajava.entities.Employer;
import com.jadajabajava.repositories.EmployerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class EmployerService extends AbstractService<Employer> {

    private final EmployerRepository employerRepository;

    @Autowired
    public EmployerService(EmployerRepository repository) {
        this.repository = repository;
        this.employerRepository = repository;
    }

    public Optional<Employer> findByRemoteId(Long remoteId) {
        return employerRepository.findByRemoteId(remoteId);
    }
}
