package net.thumbtack.school.buscompany.api;

import lombok.extern.slf4j.Slf4j;
import net.thumbtack.school.buscompany.dto.EmptyDto;
import net.thumbtack.school.buscompany.dto.request.GetTripParamsDtoRequest;
import net.thumbtack.school.buscompany.dto.request.TripDtoRequest;
import net.thumbtack.school.buscompany.dto.response.ListTripDtoResponse;
import net.thumbtack.school.buscompany.dto.response.TripDtoResponse;
import net.thumbtack.school.buscompany.exception.PathException;
import net.thumbtack.school.buscompany.exception.ServerException;
import net.thumbtack.school.buscompany.service.TripService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/api/trips", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class TripEndPoint {

    private final TripService tripService;

    public TripEndPoint(TripService tripService)
    {
        this.tripService = tripService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public TripDtoResponse registerTrip(@CookieValue("JAVASESSIONID") String javaSessionId,
                                        @RequestBody @Valid TripDtoRequest request) throws ServerException {
        log.debug("Trip end point register Trip javaSessionId:{}, dto:{}", javaSessionId, request);
        return tripService.registerTrip(request, javaSessionId);
    }

    @PutMapping(value = "/{idTrip}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public TripDtoResponse updateTrip(@CookieValue("JAVASESSIONID") String javaSessionId,
                                      @PathVariable("idTrip") int idTrip,
                                      @RequestBody @Valid TripDtoRequest request) throws ServerException, PathException {
        log.debug("Trip end point update Trip with id:{}, javaSessionId:{}, dto:{}", idTrip, javaSessionId, request);
        return tripService.updateTrip(request, javaSessionId, idTrip);

    }

    @DeleteMapping(value = "/{idTrip}")
    public EmptyDto deleteTrip(@CookieValue("JAVASESSIONID") String javaSessionId,
                               @PathVariable("idTrip") int idTrip) throws ServerException, PathException {
        log.debug("Trip end point delete Trip with id:{}, javaSessionId:{}", idTrip, javaSessionId);
        return tripService.deleteTrip(javaSessionId, idTrip);

    }

    @GetMapping(value = "/{idTrip}")
    public TripDtoResponse getTripById (@CookieValue("JAVASESSIONID") String javaSessionId,
                                        @PathVariable("idTrip") int idTrip) throws ServerException, PathException {

        log.debug("Trip end point get Trip by id:{}, javaSessionId:{}", idTrip, javaSessionId);

        return tripService.getTripById(javaSessionId, idTrip);
    }

    @PutMapping(value = "/{idTrip}/approve")
    public TripDtoResponse approvedTrip(@CookieValue("JAVASESSIONID") String javaSessionId,
                                        @PathVariable("idTrip") int idTrip) throws ServerException, PathException {
        log.debug("Trip end point approved Trip by id:{}, javaSessionId:{}", idTrip, javaSessionId);
        return tripService.setApprovedTrip(javaSessionId, idTrip);
    }

    @GetMapping
    public ListTripDtoResponse getTrips(@CookieValue("JAVASESSIONID") String javaSessionId,
                                        @Valid GetTripParamsDtoRequest request ) throws ServerException {

        log.debug("Trip end point get Trips by:{} with param: {}", javaSessionId, request);
        return tripService.getTripsWithParams(request, javaSessionId);

    }



}
