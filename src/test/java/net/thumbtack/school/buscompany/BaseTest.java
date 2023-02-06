package net.thumbtack.school.buscompany;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.thumbtack.school.buscompany.dto.EmptyDto;
import net.thumbtack.school.buscompany.dto.request.*;
import net.thumbtack.school.buscompany.dto.response.*;
import net.thumbtack.school.buscompany.models.Schedule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class BaseTest {

    @BeforeEach
    public void clearBefore(){
        clear();
    }

    @AfterEach
    public void clearAfter() {
        clear();
    }

    protected final RestTemplate template = new RestTemplate();
    private final String prefix = "http://localhost:8080";
    @Autowired
    protected ObjectMapper objectMapper;

    //Admin
    protected ResponseEntity<AdminDtoResponse> registerAdmin(RegAdminDtoRequest dto){
        RequestEntity<RegAdminDtoRequest> request = RequestEntity.post(prefix+"/api/admins").body(dto);
        return template.exchange(request, AdminDtoResponse.class);
    }

    protected ResponseEntity<AdminDtoResponse> loginAdmin(LoginDtoRequest dto){
        RequestEntity<LoginDtoRequest> request = RequestEntity.post(prefix+"/api/sessions" )
                .body( dto );
        return template.exchange(request, AdminDtoResponse.class);
    }

    protected ResponseEntity<AdminDtoResponse> updateAdmin(String javaSessionId, UpdateAdminDtoRequest dto){

        RequestEntity<UpdateAdminDtoRequest> request = RequestEntity.put(prefix+"/api/admins")
                                                                    .headers( setCookie(javaSessionId))
                                                                    .body( dto);

        return template.exchange(request, AdminDtoResponse.class);
    }

    protected ResponseEntity<ListClientDtoResponse> getAllClient(String javaSessionId){

        return template.exchange(RequestEntity.get(prefix+"/api/clients").headers(setCookie(javaSessionId)).build(), ListClientDtoResponse.class);

    }

    protected ResponseEntity<ListBusDtoResponse> getAllBuses(String javaSessionId){
        return template.exchange(RequestEntity.get(prefix+"/api/buses").headers(setCookie(javaSessionId)).build(), ListBusDtoResponse.class);
    }

    protected ResponseEntity<TripDtoResponse> registerTrip(String javaSessionId, TripDtoRequest dto){
        RequestEntity<TripDtoRequest> request = RequestEntity.post(prefix+"/api/trips")
                .headers( setCookie(javaSessionId))
                .body( dto);
        return template.exchange(request, TripDtoResponse.class);
    }

    protected ResponseEntity<TripDtoResponse> updateTrip(String javaSessionId, int idTrip, TripDtoRequest dto){
        RequestEntity<TripDtoRequest> request = RequestEntity.put(prefix+"/api/trips/"+idTrip)
                .headers( setCookie(javaSessionId))
                .body( dto);
        return template.exchange(request, TripDtoResponse.class);
    }

    protected ResponseEntity<TripDtoResponse> getTripById(String javaSessionId, int idTrip){
        return template.exchange(RequestEntity.get(prefix+"/api/trips/"+idTrip).headers(setCookie(javaSessionId)).build(), TripDtoResponse.class);
    }

    protected void deleteTrip(String javaSessionId, int idTrip){
        template.exchange(RequestEntity.delete(prefix+"/api/trips/"+idTrip).headers(setCookie(javaSessionId)).build(), EmptyDto.class);
    }

    protected ResponseEntity<TripDtoResponse> approveTrip(String javaSessionId, int idTrip){
        return template.exchange(RequestEntity.put(prefix+"/api/trips/"+idTrip+"/approve").headers(setCookie(javaSessionId)).build(), TripDtoResponse.class);
    }


    //Client
    protected ResponseEntity<ClientDtoResponse> registerClient(RegClientDtoRequest dto){
        RequestEntity<RegClientDtoRequest> request = RequestEntity.post(prefix+"/api/clients").body(dto);
        return template.exchange(request, ClientDtoResponse.class);
    }

    protected ResponseEntity<ClientDtoResponse> updateClient(String javaSessionId, UpdateClientDtoRequest dto){

        RequestEntity<UpdateClientDtoRequest> request = RequestEntity.put(prefix+"/api/clients")
                .headers( setCookie(javaSessionId))
                .body( dto);

        return template.exchange(request, ClientDtoResponse.class);
    }

    protected ResponseEntity<ClientDtoResponse> loginClient(LoginDtoRequest dto){
        RequestEntity<LoginDtoRequest> request = RequestEntity.post(prefix+"/api/sessions" )
                .body( dto );
        return template.exchange(request, ClientDtoResponse.class);
    }

    protected ResponseEntity<OrderDtoResponse> registerOrder(String javaSessionId, RegOrderRequest dto){
        RequestEntity<RegOrderRequest> request = RequestEntity.post(prefix+"/api/orders").headers(setCookie(javaSessionId)).body(dto);
        return template.exchange(request, OrderDtoResponse.class);
    }

    protected ResponseEntity<SelectPlaceDtoResponse> selectPlace(String javaSessionId, SelectPlaceDtoRequest dto){
        RequestEntity<SelectPlaceDtoRequest> request = RequestEntity.post(prefix+"/api/places").headers(setCookie(javaSessionId)).body(dto);
        return template.exchange(request, SelectPlaceDtoResponse.class);
    }

    protected ResponseEntity<FreePlacesDtoResponse> getFreePlaces(String javaSessionId, int idOrder){
        return template.exchange(RequestEntity.get(prefix+"/api/places/"+idOrder).headers(setCookie(javaSessionId)).build(), FreePlacesDtoResponse.class);
    }

    //User
    protected void logout(String javaSessionId){
        template.exchange(RequestEntity.delete(prefix+"/api/sessions").headers(setCookie(javaSessionId)).build(), EmptyDto.class);
    }

    protected void exit(String javaSessionId){
        template.exchange(RequestEntity.delete(prefix+"/api/accounts").headers(setCookie(javaSessionId)).build(), EmptyDto.class);
    }

    protected ResponseEntity<ListTripDtoResponse> getTripsWithParam(String javaSessionId, GetTripParamsDtoRequest dto){
        UriComponentsBuilder u = UriComponentsBuilder.fromHttpUrl(prefix+"/api/trips");
        if(dto.getFromStation() != null){
            u.queryParam("fromStation", dto.getFromStation());
        }
        if(dto.getToStation() != null){
            u.queryParam("toStation", dto.getToStation());
        }
        if(dto.getFromDate() != null){
            u.queryParam("fromDate", dto.getFromDate());
        }
        if(dto.getToDate() != null){
            u.queryParam("toDate", dto.getToDate());
        }
        if(dto.getBusName() != null){
            u.queryParam("busName", dto.getBusName());
        }
        String url = u.build().toString();

        return template.exchange(RequestEntity.get(url).headers(setCookie(javaSessionId)).build(), ListTripDtoResponse.class);
    }


    protected ResponseEntity<ListOrderDtoResponse> getOrdersWithParam(String javaSessionId, GetOrderParamsDtoRequest dto){
        UriComponentsBuilder u = UriComponentsBuilder.fromHttpUrl(prefix+"/api/orders");
        if(dto.getFromStation() != null){
            u.queryParam("fromStation", dto.getFromStation());
        }
        if(dto.getToStation() != null){
            u.queryParam("toStation", dto.getToStation());
        }
        if(dto.getFromDate() != null){
            u.queryParam("fromDate", dto.getFromDate());
        }
        if(dto.getToDate() != null){
            u.queryParam("toDate", dto.getToDate());
        }
        if(dto.getBusName() != null){
            u.queryParam("busName", dto.getBusName());
        }
        if (dto.getClientId() != null){
            u.queryParam("clientId", dto.getClientId());
        }
        String url = u.build().toString();

        return template.exchange(RequestEntity.get(url).headers(setCookie(javaSessionId)).build(), ListOrderDtoResponse.class);
    }
    //util
    protected void clear(){
        template.delete(prefix+"/api/debug/clear");
    }


    protected String registerAdminDanila(){
        RegAdminDtoRequest request = new RegAdminDtoRequest(
                "shnayderdanila", "e365025", "Данила", "Шнайдер", "Владимирович", "admin");

        return registerAdmin(request).getHeaders().getFirst(HttpHeaders.SET_COOKIE).split("=")[1];
    }

    protected String registerClientIvan(){
        RegClientDtoRequest request = new RegClientDtoRequest(
                "ivanstrelnikov", "e365025", "Иван", "Стрельников", "Витальевич", "str@mail.ru","89836232211");

        return registerClient(request).getHeaders().getFirst(HttpHeaders.SET_COOKIE).split("=")[1];
    }

    protected TripDtoResponse registerScheduledTrip(String javaSessionId){

        Schedule schedule = new Schedule("2022-06-01", "2022-06-08", "1, 2, 3");

        TripDtoRequest tripDtoRequest = new TripDtoRequest("Автобус", "Омск", "Таврическое",
                "13:45", "01:00", 150.0, schedule, null);

        return registerTrip(javaSessionId, tripDtoRequest).getBody();

    }

    private HttpHeaders setCookie(String javaSessionId){
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie","JAVASESSIONID="+javaSessionId);
        return headers;
    }


}
