package com.senla.task1.dao;

import com.senla.task1.models.Mechanic;

import java.util.List;

public interface MechanicDAO extends GenericDAO<Mechanic, Integer> {

    List<Mechanic> sortBy(String field, boolean flag);

    Boolean checkIsMechanicExists(Integer id);

    void importWithTransaction(List<Mechanic> mechanicList);
}
