package net.thumbtack.school.buscompany.configuration;

import net.thumbtack.school.buscompany.dao.CookieDao;
import net.thumbtack.school.buscompany.service.CookieHandler;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


@Configuration
@Import(DatabaseConfig.class)
@EnableCaching
public class AppConfig {

    private final CookieDao cookieDao;

    public AppConfig(CookieDao cookieDao) {
        this.cookieDao = cookieDao;
    }

    @Bean
    public CookieHandler cookieHandler(){
        CookieHandler cookieHandler =  new CookieHandler(cookieDao);
        cookieHandler.start();
        return cookieHandler;
    }

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("busses");
    }

}
