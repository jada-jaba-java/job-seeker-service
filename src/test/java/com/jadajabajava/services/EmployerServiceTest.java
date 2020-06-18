package com.jadajabajava.services;

import com.jadajabajava.entities.Employer;
import com.jadajabajava.repositories.EmployerRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployerServiceTest {

    @Mock
    private EmployerRepository employerRepository;

    @InjectMocks
    private EmployerService employerService;

    private static Employer existingEmployer;

    @BeforeAll
    static void setUp() {
        Long existingEmployerId = 3L;
        Long existingEmployerRemoteId = 12L;
        String existingEmployerTitle = "Cowabunga LLC";

        existingEmployer = Employer
                .builder()
                .id(existingEmployerId)
                .remoteId(existingEmployerRemoteId)
                .title(existingEmployerTitle)
                .build();
    }

    @Test
    void findByRemoteId_ReturnsExactValueFromRepository() {
        Long remoteIdThatMotExists = 7L;

        when(employerRepository.findByRemoteId(existingEmployer.getRemoteId()))
                .thenReturn(Optional.ofNullable(existingEmployer));
        when(employerRepository.findByRemoteId(remoteIdThatMotExists))
                .thenReturn(Optional.empty());

        Optional<Employer> actualEmployer = employerService.findByRemoteId(existingEmployer.getRemoteId());
        assertTrue(actualEmployer.isPresent());
        assertEquals(existingEmployer, actualEmployer.get());
        verify(employerRepository).findByRemoteId(existingEmployer.getRemoteId());

        Optional<Employer> notExistsEmployer = employerService.findByRemoteId(remoteIdThatMotExists);
        assertFalse(notExistsEmployer.isPresent());
        verify(employerRepository).findByRemoteId(remoteIdThatMotExists);
    }
}
