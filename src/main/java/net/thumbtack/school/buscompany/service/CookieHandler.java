package net.thumbtack.school.buscompany.service;

import net.thumbtack.school.buscompany.dao.CookieDao;
import net.thumbtack.school.buscompany.exception.ServerException;
import net.thumbtack.school.buscompany.utils.SimpleFormatter;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class CookieHandler extends Thread{
    private final CookieDao cookieDao;

    public CookieHandler(CookieDao cookieDao) {
        this.cookieDao = cookieDao;
    }

    @Override
    public void run() {

        while (true){
            try {
                Thread.sleep(10000);
                cookieDao.checkAndDeleteJavaSessionByTime(
                        SimpleFormatter.dateWithTimeFormatter.format(
                                LocalDateTime.now(ZoneId.of("GMT"))
                        )
                );
            } catch (ServerException e) {
                // попытка перезапустить или остановка приложения?
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }
}
