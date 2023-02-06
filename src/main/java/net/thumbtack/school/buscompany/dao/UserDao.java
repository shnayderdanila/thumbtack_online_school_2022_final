package net.thumbtack.school.buscompany.dao;

import net.thumbtack.school.buscompany.exception.ServerException;
import net.thumbtack.school.buscompany.models.User;



public interface UserDao {
    void login(User user, String javaSessionId, String date) throws  ServerException;

    User getUserByLogin(String login) throws ServerException;

    User getUserByJavaSessionId(String javaSessionId) throws ServerException;

    void updateSessionId(String javaSessionId, String time) throws  ServerException;

    void deleteById(int id) throws  ServerException;

    String getLoginByLogin(String login) throws ServerException;

}
