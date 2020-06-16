package com.jadajabajava.services;

import com.jadajabajava.entities.AbstractEntity;
import com.jadajabajava.exceptions.NotExistException;
import com.jadajabajava.repositories.GenericRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

@Slf4j
abstract public class AbstractService<T extends AbstractEntity> {
    protected GenericRepository<T> repository;
    protected MessageSource messageSource;

    public T getById(Long id) throws NotExistException {
        return repository.findById(id).orElseThrow(() -> {
            String exceptionMessage = messageSource.getMessage(
                    "exception.entity.not.exist", new Object[] { id }, LocaleContextHolder.getLocale());
            log.error(exceptionMessage);
            return new NotExistException(exceptionMessage);
        });
    }

    public T save(T obj) {
        log.debug("Saving entity:\n{}", obj);
        return repository.save(obj);
    }

    public T update(T obj) {
        log.debug("Updating entity:\n{}", obj);
        return repository.save(obj);
    }

    public void delete(T obj) {
        log.debug("Deleting entity:\n{}", obj);
        repository.delete(obj);
    }
}
