package net.thumbtack.school.buscompany.dao;

import net.thumbtack.school.buscompany.exception.ServerException;

public interface CookieDao {

    void checkAndDeleteJavaSessionByTime(String now) throws ServerException;
    void deleteJavaSessionById(int id) throws ServerException;

}
