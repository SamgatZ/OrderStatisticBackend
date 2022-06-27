package com.technodom.OrderStatisticBackend.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Order {

    public Long id;

    public String orderNum;

    public Instant changedDate;

    public Instant deliverFrom;

    public Instant deliverTo;

    public String status;



}
