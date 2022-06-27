package com.technodom.OrderStatisticBackend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(value = "technodomapi")
@Data
public class TDConfig {
    private String completedUrl;
    private String outForDeliveryUrl;
    private String newUrl;
    private String delayedUrl;
    private String cancelledUrl;
}
