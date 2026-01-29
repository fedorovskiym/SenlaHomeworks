package com.senla.task1.dao;

import java.io.Serializable;
import java.util.List;

public interface GenericDAO<T extends Serializable, ID> {

    List<T> findAll();

    void save(T entity);

    void delete(ID id);

    void update(T entity);
}
