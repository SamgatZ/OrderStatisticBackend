package com.technodom.OrderStatisticBackend.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.technodom.OrderStatisticBackend.config.ApiAuthConfig;
import com.technodom.OrderStatisticBackend.config.TDConfig;
import com.technodom.OrderStatisticBackend.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.HOURS;


@RestController
@RequestMapping(value = "/api")
@RequiredArgsConstructor
public class OrderController {
    private final RestTemplate restTemplate;
    private final TDConfig tdConfig;
    private final ApiAuthConfig authConfig;

    @GetMapping(value = "/orderstatistic")
    public StatisticResponse getOutForDeliveryOrders() throws JsonProcessingException {
        String token;
        HttpHeaders header;
        Instant currentInstant;
        List<Order> completedOrders;
        HttpEntity<String> httpEntity;
        List<JSONResponse> completedOrdersJsonResponse;
        List<JSONResponse> outForDeliveryOrdersList;
        Clock offsetClock;
        long lateOrdersCount;
        long newOrdersCount;
        long delayedOrdersCount;
        long outForDeliveryCount;
        long cancelledOrdersCount;

        header = new HttpHeaders();
        token = authenticate();
        header.set("Authorization",token);
        httpEntity = new HttpEntity<>(header);
        offsetClock = Clock.offset(Clock.systemUTC(), Duration.ofHours(+6));
        currentInstant = Instant.now(offsetClock);
        completedOrdersJsonResponse = getJSONResponseList(tdConfig.getCompletedUrl().replace("currentInstant",String.valueOf(currentInstant)),httpEntity);
        completedOrders = completedOrdersJsonResponse.stream().map(JSONResponse::getOrder).collect(Collectors.toList());
        lateOrdersCount = completedOrders.stream().filter(a->a.changedDate.plus(6,HOURS).isAfter(a.deliverTo) || a.changedDate.plus(6,HOURS).isBefore(a.deliverFrom)).count();

        outForDeliveryOrdersList = getJSONResponseList(tdConfig.getOutForDeliveryUrl().replace("currentInstant",String.valueOf(currentInstant)),httpEntity);
        outForDeliveryCount = outForDeliveryOrdersList.size();

        newOrdersCount = getJSONResponseList(tdConfig.getNewUrl().replace("currentInstant",String.valueOf(currentInstant)),httpEntity).size();

        delayedOrdersCount = getJSONResponseList(tdConfig.getDelayedUrl().replace("currentInstant",String.valueOf(currentInstant)),httpEntity).size();

        cancelledOrdersCount = getJSONResponseList(tdConfig.getCancelledUrl().replace("currentInstant",String.valueOf(currentInstant)),httpEntity).size();

        return new StatisticResponse(newOrdersCount,outForDeliveryCount,delayedOrdersCount,lateOrdersCount,cancelledOrdersCount);
    }

    public String authenticate() throws JsonProcessingException {
        ApiAuthentication authenticationUser;
        String authenticationBody;
        String token;
        HttpHeaders authenticationHeaders;
        HttpEntity<String> authenticationEntity;
        ResponseEntity<String> authentication;
        AuthenticationToken authenticationToken;

        authenticationUser = new ApiAuthentication(authConfig.getUserName(),authConfig.getPassword(),false);
        authenticationBody = new ObjectMapper().writeValueAsString(authenticationUser);
        authenticationHeaders = new HttpHeaders();
        authenticationHeaders.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        authenticationHeaders.set("Accept", MediaType.ALL_VALUE);
        authenticationEntity = new HttpEntity<>(authenticationBody, authenticationHeaders);
        authentication = restTemplate.exchange(authConfig.getUrl(), HttpMethod.POST, authenticationEntity,String.class);
        authenticationToken = new AuthenticationToken();
        authenticationToken.setToken(String.valueOf(authentication.getHeaders().getOrEmpty("Authorization")));
        token = authenticationToken.getToken().replace("[","").replace("]","").trim();
        return token;
    }


    public List<JSONResponse> getJSONResponseList(String url, HttpEntity<String> httpEntity) throws JsonProcessingException {
        int i = 0;
        List<JSONResponse> list = new ArrayList<>();

        while (true){
            ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules().disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            ResponseEntity<String> response = restTemplate.exchange(url + i, HttpMethod.GET, httpEntity, String.class);
            List<JSONResponse> pageList = objectMapper.readValue(response.getBody(), new TypeReference<>() {
            });
            if (pageList.size() == 0) {
                break;
            } else {
                list.addAll(pageList);
            }
            i++;
        }
        return list;
    }

}




