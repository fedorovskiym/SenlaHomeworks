package com.senla.task1.repository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public interface GenericRepository<T, PK extends Serializable> {

    List<T> findAll();

    void save(T entity);

    void delete(T entity);

    void update(T entity);

    Optional<T> findById(PK id);
}
