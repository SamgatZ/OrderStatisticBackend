package com.technodom.OrderStatisticBackend.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatisticResponse {

    private long newOrders;

    private long outForDeliveryOrders;

    private long delayedOrders;

    private long lateOrders;

    private long cancelledOrders;

}
