package net.thumbtack.school.buscompany;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.thumbtack.school.buscompany.dto.request.*;
import net.thumbtack.school.buscompany.dto.response.TripDtoResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MultiThreadTest extends BaseTest {



    @Test
    public void testMultiThreadRegisterOrder() throws InterruptedException {
        int totalClient = 30;

        String          adminSession   = registerAdminDanila();
        TripDtoResponse trip           = registerScheduledTrip(adminSession);

        approveTrip(adminSession, trip.getIdTrip());

        List<RegPassengerDto> passengers = new ArrayList<>();
        passengers.add(new RegPassengerDto("Шнайдер", "Данила", "123123123"));

        List<String> cookieClients = new ArrayList<>();

        for(int i = 0; i < totalClient; i++){
            cookieClients.add(
                    registerClient(new RegClientDtoRequest("shnayderdanila"+i, "e365025", "Данила", "Шнайдер",
                                                       "Владимирович", "shnayder"+i+"@mail.ru", "88005553535")
                    ).getHeaders().getFirst(HttpHeaders.SET_COOKIE).split("=")[1]
            );
        }

        CountDownLatch countDownLatch = new CountDownLatch(totalClient);

        ExecutorService executorService = Executors.newCachedThreadPool();

        cookieClients.forEach(
                session -> executorService.execute(() -> {
                                registerOrder(session, new RegOrderRequest(trip.getIdTrip(), "2022-06-01", passengers)).getBody();
                                countDownLatch.countDown();
                })
        );

        countDownLatch.await ();
        executorService.shutdown();

        GetOrderParamsDtoRequest getAllOrder = new GetOrderParamsDtoRequest( null, null, null, null, null, null);

        assertEquals(totalClient, getOrdersWithParam(adminSession, getAllOrder).getBody().getList().size());

    }

    @Test
    public void testWrongMultiThreadRegisterOrder() throws JsonProcessingException, InterruptedException {
        int totalClient = 31;

        String          adminSession   = registerAdminDanila();
        TripDtoResponse trip           = registerScheduledTrip(adminSession);

        approveTrip(adminSession, trip.getIdTrip());

        List<RegPassengerDto> passengers = new ArrayList<>();
        passengers.add(new RegPassengerDto("Шнайдер", "Данила", "123123123"));

        List<String> cookieClients = new ArrayList<>();

        for(int i = 0; i < totalClient; i++){
            cookieClients.add(
                    registerClient(new RegClientDtoRequest("shnayderdanila"+i, "e365025", "Данила", "Шнайдер",
                            "Владимирович", "shnayder"+i+"@mail.ru", "88005553535")
                    ).getHeaders().getFirst(HttpHeaders.SET_COOKIE).split("=")[1]
            );
        }

        ExecutorService executorService = Executors.newCachedThreadPool();
        CountDownLatch countDownLatch = new CountDownLatch(totalClient);

        cookieClients.forEach(
                session -> executorService.execute(() -> {
                                try {
                                    registerOrder(session, new RegOrderRequest(trip.getIdTrip(), "2022-06-01", passengers)).getBody();
                                    countDownLatch.countDown();
                                } catch (HttpClientErrorException e) {
                                    countDownLatch.countDown();
                                }
                })
        );
        countDownLatch.await();
        executorService.shutdown();

        GetOrderParamsDtoRequest getAllOrder = new GetOrderParamsDtoRequest( null, null, null, null, null, null);

        assertEquals(totalClient -1 , getOrdersWithParam(adminSession, getAllOrder).getBody().getList().size());
    }


    @Test
    public void testSelectPlaceMultiThread() throws InterruptedException {
        int totalClient = 30;

        String          adminSession   = registerAdminDanila();
        TripDtoResponse trip           = registerScheduledTrip(adminSession);

        approveTrip(adminSession, trip.getIdTrip());

        List<RegPassengerDto> passengers = new ArrayList<>();
        passengers.add(new RegPassengerDto("Шнайдер", "Данила", "123123123"));

        List<String> cookieClients = new ArrayList<>();

        for(int i = 0; i < totalClient; i++){
            cookieClients.add(
                    registerClient(new RegClientDtoRequest("shnayderdanila"+i, "e365025", "Данила", "Шнайдер",
                            "Владимирович", "shnayder"+i+"@mail.ru", "88005553535")
                    ).getHeaders().getFirst(HttpHeaders.SET_COOKIE).split("=")[1]
            );
        }

        ExecutorService                executorService    = Executors.newCachedThreadPool();
        CountDownLatch                 registerOrderCount = new CountDownLatch(totalClient);
        CountDownLatch                 selectPlaceCount   = new CountDownLatch(totalClient);
        ConcurrentMap<String, Integer> clientToOrder      = new ConcurrentHashMap<>();

        cookieClients.forEach(
                session -> executorService.execute(
                        () -> {
                            clientToOrder.put(session, registerOrder(session, new RegOrderRequest(trip.getIdTrip(), "2022-06-01", passengers)).getBody().getIdOrder());
                            registerOrderCount.countDown();
                        }
                )
        );

        registerOrderCount.await();

        clientToOrder.forEach(
                (session, idOrder) -> {
                    for(int i = 0; i < totalClient; i++){
                        int finalI = i + 1;
                        executorService.execute(
                                () -> {
                                    selectPlace(session, new SelectPlaceDtoRequest(idOrder, "Шнайдер", "Данила", "123123123", finalI));
                                    selectPlaceCount.countDown();
                                }
                        );
                    }
                }
        );

        selectPlaceCount.await();

        clientToOrder.forEach(
                (session, idOrder) -> {
                    Set<Integer> response = getFreePlaces(session, idOrder).getBody().getPlaces();
                    assertEquals(1, response.size());
                    assertTrue(response.contains(0));
                }
        );

    }

}
