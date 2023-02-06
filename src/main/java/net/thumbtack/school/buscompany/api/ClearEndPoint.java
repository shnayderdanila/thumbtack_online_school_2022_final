package net.thumbtack.school.buscompany.api;

import net.thumbtack.school.buscompany.dao.CommonDao;
import net.thumbtack.school.buscompany.dto.EmptyDto;
import net.thumbtack.school.buscompany.exception.ServerException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/debug/clear", produces = MediaType.APPLICATION_JSON_VALUE)
public class ClearEndPoint {

    private final CommonDao dao;

    public ClearEndPoint(CommonDao dao) {
        this.dao = dao;
    }
    
    @DeleteMapping
    public EmptyDto clear() throws ServerException {
        dao.clear();
        return new EmptyDto();
    }
}
