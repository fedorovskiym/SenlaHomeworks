package com.senla.task1.repository;

import com.senla.task1.models.GaragePlace;

import java.util.List;
import java.util.Optional;

public interface GaragePlaceRepository extends GenericRepository<GaragePlace, Integer> {
    Boolean checkIsPlaceNumberExists(int placeNumber);

    List<GaragePlace> findFreeGaragePlaces();

    Optional<GaragePlace> findByPlaceNumber(int placeNumber);

    Optional<GaragePlace> findById(Integer id);

    void importWithTransaction(List<GaragePlace> garagePlaces);
}
