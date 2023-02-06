package net.thumbtack.school.buscompany.dao;

import net.thumbtack.school.buscompany.exception.ServerException;
import net.thumbtack.school.buscompany.models.Admin;

public interface AdminDao {
    Admin insert(Admin administrator) throws ServerException;

    void update(Admin administrator) throws ServerException;

    Integer countAdmin() throws ServerException;
}
