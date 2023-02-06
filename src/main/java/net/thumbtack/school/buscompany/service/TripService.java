package net.thumbtack.school.buscompany.service;

import lombok.extern.slf4j.Slf4j;
import net.thumbtack.school.buscompany.dao.BusDao;
import net.thumbtack.school.buscompany.dao.TripDao;
import net.thumbtack.school.buscompany.dto.EmptyDto;
import net.thumbtack.school.buscompany.dto.request.GetTripParamsDtoRequest;
import net.thumbtack.school.buscompany.dto.request.TripDtoRequest;
import net.thumbtack.school.buscompany.dto.response.ListTripDtoResponse;
import net.thumbtack.school.buscompany.dto.response.TripDtoResponse;
import net.thumbtack.school.buscompany.exception.ErrorCode;
import net.thumbtack.school.buscompany.exception.PathException;
import net.thumbtack.school.buscompany.exception.ServerException;
import net.thumbtack.school.buscompany.mappers.mapstruct.DateConverter;
import net.thumbtack.school.buscompany.mappers.mapstruct.TripConverter;
import net.thumbtack.school.buscompany.models.*;
import net.thumbtack.school.buscompany.utils.SimpleFormatter;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TripService extends BaseService{

    private final TripDao tripDao;
    private final BusDao  busDao;

    public TripService(TripDao tripDao, BusDao busDao) {
        this.tripDao = tripDao;
        this.busDao  = busDao;
    }

    public TripDtoResponse registerTrip(TripDtoRequest request, String javaSessionId) throws ServerException {

        Admin admin = baseGetAdminByJavaSessionId(javaSessionId);

        Trip trip = TripConverter.MAPPER.dtoToTrip(request);
        log.info("Trip service register Trip:{} by Admin:{}", trip, admin);

        trip.setBus(getBusByName(request.getBusName()));

        if(trip.getSchedule() != null){
            trip.setDates(getDatesViaSchedule(trip));
        }

        List<Integer> places = new ArrayList<>();
        for(int i = 1; i < trip.getBus().getPlaceCount() + 1; i++){
            places.add(i);
        }

        tripDao.insert(trip, places);

        return TripConverter.MAPPER.tripToDto(trip);
    }

    public TripDtoResponse updateTrip(TripDtoRequest updateTrip, String javaSessionId, int idTrip) throws ServerException, PathException {

        Trip trip = baseGetTripById(idTrip);
        Admin admin = baseGetAdminByJavaSessionId(javaSessionId);

        log.info("Trip service update Trip {}, {} by Admin:{}", trip, updateTrip, admin);

        checkApproved(trip);
        TripConverter.MAPPER.update(trip, updateTrip);

        if( updateTrip.getSchedule() != null &&
           !trip.getSchedule().equals( updateTrip.getSchedule() ))
        {
            trip.setDates(getDatesViaSchedule(trip));
        }

        if (!trip.getBus().getBusName().equals(updateTrip.getBusName()))
        {
            trip.setBus(getBusByName(updateTrip.getBusName()));
        }

        List<Integer> places = new ArrayList<>();
        for(int i = 1; i < trip.getBus().getPlaceCount() + 1; i++){
            places.add(i);
        }
        tripDao.update(trip, places);

        return TripConverter.MAPPER.tripToDto(trip);
    }

    public TripDtoResponse setApprovedTrip(String javaSessionId, int idTrip) throws ServerException, PathException {

        Admin admin = baseGetAdminByJavaSessionId(javaSessionId);
        Trip trip = baseGetTripById(idTrip);

        log.info("Trip service approved Trip:{} by Admin:{} ", trip, admin);

        checkApproved(trip);
        trip.setApproved(true);
        tripDao.setApprovedTrip(trip);

        return TripConverter.MAPPER.tripToDto(trip);
    }

    public EmptyDto deleteTrip(String javaSessionId, int idTrip) throws ServerException, PathException {

        Admin admin = baseGetAdminByJavaSessionId(javaSessionId);
        Trip trip = baseGetTripById(idTrip);

        log.info("Trip service delete Trip:{} by Admin:{}", trip, admin);

        checkApproved(trip);
        tripDao.delete(trip);

        return new EmptyDto();
     }

    public TripDtoResponse getTripById(String javaSessionId, int idTrip) throws PathException, ServerException {

        Admin admin = baseGetAdminByJavaSessionId(javaSessionId);
        log.debug("Trip service get by Admin:{} Trip with id:{}", admin, idTrip);

        return TripConverter.MAPPER.tripToDto(baseGetTripById(idTrip));

    }

    public ListTripDtoResponse getTripsWithParams(GetTripParamsDtoRequest request, String javaSessionId) throws ServerException {
        User user = baseGetUserByJavaSessionId(javaSessionId);

        log.info("Trip service get Trips with param:{} by User:{}", request, user);
        List<Trip> trips = tripDao.getTripsWithParam(request);

        if (user.getUserType() == UserType.ADMIN){
            return new ListTripDtoResponse(
                    trips.stream()
                            .map(TripConverter.MAPPER::tripToDto)
                            .collect(Collectors.toList()));
        }
        else {
            return new ListTripDtoResponse(
                    trips.stream()
                            .filter( Trip::isApproved )
                            .map( TripConverter.MAPPER::tripToDto )
                            .collect( Collectors.toList() ));
        }
    }

    private List<TripDate> getDatesViaSchedule(Trip trip) throws ServerException {
        Schedule schedule = trip.getSchedule();

        log.debug("Trip service get dates via Schedule:{}", schedule);

        String period = schedule.getPeriod();

        LocalDate fromDate = LocalDate.parse(schedule.getFromDate());
        LocalDate toDate   = LocalDate.parse(schedule.getToDate());

        Predicate<LocalDate> filter = getFilter(period);

        List<TripDate> dates = new ArrayList<>();

        log.debug("Trip service get dates via Schedule start loop");
        for (LocalDate it = fromDate; it.isBefore(toDate.plusDays(1)); it = it.plusDays(1)){

            if(filter.test(it)){

                dates.add(new TripDate(0, trip, SimpleFormatter.dateFormatter.format(it)));

            }
        }

        if(dates.isEmpty()){
            throw new ServerException(ErrorCode.INCORRECT_SCHEDULE_PERIOD, "The period "+period+" consists of an empty list of dates.");
        }

        log.info("Trip service get dates via Schedule return:{}", dates);
        return dates;
    }


    private void checkApproved(Trip trip) throws ServerException {
        if(trip.isApproved()){
            throw new ServerException(ErrorCode.WRONG_ACTION, "You can not delete/update approved Trip:"+trip.getId());
        }
    }

    private  Predicate<LocalDate> getDailyFilter(){
        return x -> true;
    }

    private  Predicate<LocalDate> getOddFilter(){
        return x -> x.getDayOfMonth() % 2 == 1;
    }

    private  Predicate<LocalDate> getEvenFilter(){
        return Predicate.not(getOddFilter());
    }

    private  Predicate<LocalDate> getWeekFilter(List<DayOfWeek> days){
        return date -> {

            for (DayOfWeek day : days) {
                if (day == date.getDayOfWeek()) {

                    return true;

                }
            }
            return false;

        };
    }

    private Predicate<LocalDate> getMonthDayFilter(List<Integer> days) {
        return date -> {

            for (int day : days) {
                if (day == date.getDayOfMonth()) {
                    return true;
                }
            }
            return false;

        };
    }

    private String[] parsePeriod(String period) throws ServerException {

        log.debug("Parsing period:{}", period);

        String[] parse = period .toUpperCase()
                                .replace(" ", "")
                                .split(",");

        if(parse.length != new HashSet<>(Arrays.asList(parse)).size()){

            log.info("Incorrect period:{},{}", period, ErrorCode.INCORRECT_SCHEDULE_PERIOD);
            throw new ServerException(ErrorCode.INCORRECT_SCHEDULE_PERIOD, "Incorrect period:"+period);

        }
        return parse;

    }

    private Predicate<LocalDate> getFilter(String period) throws ServerException {

        switch (period) {

            case ("daily"):

                log.debug("Trip service get dates via Schedule get Daily Filter");
                return getDailyFilter();

            case ("odd"):

                log.debug("Trip service get dates via Schedule get Odd Filter");
                return getOddFilter();

            case ("even"):

                log.debug("Trip service get dates via Schedule get Even Filter");
                return getEvenFilter();

            default:

                String[] parse = parsePeriod(period);

                    List<DayOfWeek> weekDays = DateConverter.MAPPER.stringsToWeekDays(parse);

                    if (weekDays != null) {

                        log.debug("Trip service get dates via Schedule get Week Filter");
                        return getWeekFilter(weekDays);

                    }

                    List<Integer> dates = DateConverter.MAPPER.stringsToInt(parse);

                    if (dates != null) {

                        log.debug("Trip service get dates via Schedule get Month Filter");
                        return getMonthDayFilter(dates);

                    }

                    log.info("Trip service get dates via Schedule return error:{}", ErrorCode.INCORRECT_SCHEDULE_PERIOD);
                    throw new ServerException(ErrorCode.INCORRECT_SCHEDULE_PERIOD, "Incorrect period:" + period);

        }
    }

    private Bus getBusByName(String busName) throws ServerException {

        log.debug("Trip service get bus by name:{}", busName);

        Bus bus = busDao.getBusByName(busName);

        if(bus == null){

            throw new ServerException(ErrorCode.INCORRECT_BUS_NAME,"Bus:"+ busName +" doesn't exist");

        }

        return bus;
    }
}
