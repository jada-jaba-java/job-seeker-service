package com.jadajabajava.repositories;

import com.jadajabajava.entities.Employer;

import java.util.Optional;

public interface EmployerRepository extends GenericRepository<Employer> {

    Optional<Employer> findByRemoteId(Long remoteId);
}
