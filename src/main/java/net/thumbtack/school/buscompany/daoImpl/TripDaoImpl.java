package net.thumbtack.school.buscompany.daoImpl;

import lombok.extern.slf4j.Slf4j;
import net.thumbtack.school.buscompany.dao.TripDao;
import net.thumbtack.school.buscompany.dto.request.GetTripParamsDtoRequest;
import net.thumbtack.school.buscompany.exception.ErrorCode;
import net.thumbtack.school.buscompany.exception.ServerException;
import net.thumbtack.school.buscompany.mappers.mybatis.TripDateMapper;
import net.thumbtack.school.buscompany.mappers.mybatis.TripMapper;
import net.thumbtack.school.buscompany.models.Trip;
import net.thumbtack.school.buscompany.models.TripDate;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Primary
@Service
@Slf4j
public class TripDaoImpl implements TripDao {
    private final TripMapper     tripMapper;
    private final TripDateMapper tripDateMapper;

    public TripDaoImpl(TripMapper tripMapper, TripDateMapper tripDateMapper) {
        this.tripMapper     = tripMapper;
        this.tripDateMapper = tripDateMapper;
    }

    @Override
    @Transactional(rollbackFor = ServerException.class, propagation = Propagation.REQUIRED)
    public Trip insert(Trip trip, List<Integer> places) throws ServerException {
        log.debug("Dao insert Trip:{}", trip);
        try{
            tripMapper.insertTrip(trip, trip.getBus());
            if(trip.getSchedule() != null){
                tripMapper.insertSchedule(trip.getId(), trip.getSchedule());
            }
            tripMapper.batchInsertDate(trip, trip.getDates());

            for (TripDate tripDate : trip.getDates()){
                tripMapper.batchInsertPlace(tripDate.getId(), places);
            }

            return trip;
        }
        catch (RuntimeException e){
            log.error("Can't insert Trip:{}. Error:{}",trip, e);
            throw new ServerException(ErrorCode.DATABASE_EXCEPTION, "Can't insert Trip:"+trip);
        }
    }

    @Override
    @Transactional(rollbackFor = ServerException.class, propagation = Propagation.REQUIRED)
    public void update(Trip updateTrip, List<Integer> places) throws ServerException {
        log.debug("Dao update Trip:{}", updateTrip);
        try {

            log.debug("DAO update Trip:{}", updateTrip);
            tripMapper.updateTrip(updateTrip, updateTrip.getBus());

            if(updateTrip.getSchedule() != null){
                tripMapper.updateSchedule(updateTrip.getId(), updateTrip.getSchedule());
            }

            tripMapper.deleteDates(updateTrip.getId());

            tripMapper.batchInsertDate(updateTrip, updateTrip.getDates());

            for (TripDate tripDate : updateTrip.getDates()){
                tripMapper.batchInsertPlace(tripDate.getId(), places);
            }
        }
        catch (RuntimeException e){
            log.error("Can't update Trip:{}. Error:{}",updateTrip, e);
            throw new ServerException(ErrorCode.DATABASE_EXCEPTION, "Can't update Trip:"+updateTrip);
        }
    }

    @Override
    public void setApprovedTrip(Trip trip) throws ServerException {
        log.debug("DAO set approved Trip:{}", trip);
        try {

            tripMapper.setApprovedTrip(trip);

        } catch (RuntimeException e) {
            log.error("Can't set approved Trip:{}. Error:{}", trip, e);
            throw new ServerException(ErrorCode.DATABASE_EXCEPTION, "Can't set approved Trip:" + trip);
        }

    }

    @Override
    public void delete(Trip trip) throws ServerException {
        log.debug("DAO delete Trip:{}", trip);
        try {
            tripMapper.deleteTrip(trip);
        }
        catch (RuntimeException e){

            log.error("Can't delete Trip:{}. Error:{}",trip, e);
            throw new ServerException(ErrorCode.DATABASE_EXCEPTION, "Can't delete Trip:"+trip);

        }
    }

    @Override
    public Trip getTripById(int idTrip) throws ServerException {
        log.debug("DAO get Trip by id:{}", idTrip);
        try {
            return tripMapper.getTripById(idTrip);
        }
        catch (RuntimeException e){
            log.error("DAO get Trip return error:{}", e);
            throw new ServerException(ErrorCode.DATABASE_EXCEPTION, "Can't ger Trip by id:"+idTrip);
        }
    }

    @Override
    public TripDate getTripDateById(int id) throws ServerException {
        log.debug("DAO get TripDate by id:{}", id);
        try {
            return tripDateMapper.getTripDateById(id);
        }
        catch (RuntimeException e){
            log.error("DAO can't get TripDate by id:{}. Error:{}", id, e);
            throw new ServerException(ErrorCode.DATABASE_EXCEPTION, "Can't get TripDate by id:"+id);
        }
    }

    @Override
    public List<TripDate> getTripDateByTrip(int idTrip) throws ServerException {
        log.debug("DAO get TripDate by id Trip:{}", idTrip);
        try {
            return tripDateMapper.getTripsDateByTrip(idTrip);
        }
        catch (RuntimeException e){
            log.error("DAO can't get TripDate by id Trip:{}. Error:{}", idTrip, e);
            throw new ServerException(ErrorCode.DATABASE_EXCEPTION, "Can't get TripDate by id Trip:"+idTrip);
        }
    }

    @Override
    public List<Trip> getTripsWithParam(GetTripParamsDtoRequest map) throws ServerException {
        log.debug("DAO get Trips with Params:{}",map);
        try {
            return tripMapper.getTripsWithParam(map);
        }
        catch (RuntimeException e){
            log.error("DAO get Trip return error:{}", e);
            throw new ServerException(ErrorCode.DATABASE_EXCEPTION, "Can't get Trip with Params:"+map);
        }
    }
}
