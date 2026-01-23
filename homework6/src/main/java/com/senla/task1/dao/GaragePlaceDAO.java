package com.senla.task1.dao;

import com.senla.task1.models.GaragePlace;

import java.util.List;
import java.util.Optional;

public interface GaragePlaceDAO extends GenericDAO<GaragePlace, Integer> {
    Boolean checkIsPlaceNumberExists(int placeNumber);

    List<GaragePlace> findFreeGaragePlaces();

    Optional<GaragePlace> findByPlaceNumber(int placeNumber);

    void importWithTransaction(List<GaragePlace> garagePlaces);
}
