package net.thumbtack.school.buscompany.dao;

import net.thumbtack.school.buscompany.exception.ServerException;
import net.thumbtack.school.buscompany.models.Client;

import java.util.List;

public interface ClientDao {

    Client insert(Client client) throws ServerException;

    List<Client> getAllClient() throws ServerException;

    void update(Client client) throws ServerException;

    Integer getClientIdByEmail(String email) throws ServerException;
}
