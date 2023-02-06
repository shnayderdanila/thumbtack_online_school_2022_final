package net.thumbtack.school.buscompany.dao;

import net.thumbtack.school.buscompany.exception.ServerException;
import net.thumbtack.school.buscompany.models.Bus;

import java.util.List;

public interface BusDao {

    List<Bus> getAllBus() throws ServerException;

    Bus getBusByName(String busName) throws ServerException;

}
