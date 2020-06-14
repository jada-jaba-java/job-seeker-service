package com.jadajabajava.repositories;

import com.jadajabajava.entities.AbstractEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface GenericRepository<T extends AbstractEntity> extends JpaRepository<T, Long> {

}
