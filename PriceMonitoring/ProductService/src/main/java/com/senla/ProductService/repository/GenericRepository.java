package com.senla.ProductService.repository;

import java.util.List;
import java.util.Optional;

public interface GenericRepository<T, ID> {

    List<T> findAll();

    void save(T entity);

    void delete(T entity);

    void update(T entity);

    Optional<T> findById(ID id);
}
