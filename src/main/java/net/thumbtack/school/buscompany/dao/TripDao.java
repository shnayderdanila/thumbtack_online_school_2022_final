package net.thumbtack.school.buscompany.dao;

import net.thumbtack.school.buscompany.dto.request.GetTripParamsDtoRequest;
import net.thumbtack.school.buscompany.exception.ServerException;
import net.thumbtack.school.buscompany.models.Trip;
import net.thumbtack.school.buscompany.models.TripDate;

import java.util.List;

public interface TripDao {

    Trip insert(Trip trip, List<Integer> places) throws ServerException;

    void update(Trip trip, List<Integer> places) throws ServerException;

    void setApprovedTrip(Trip trip) throws ServerException;

    void delete(Trip trip) throws ServerException;

    Trip getTripById(int idTrip) throws ServerException;

    TripDate getTripDateById(int idTrip) throws ServerException;

    List<TripDate> getTripDateByTrip(int trip) throws ServerException;

    List<Trip> getTripsWithParam(GetTripParamsDtoRequest map) throws ServerException;
}
