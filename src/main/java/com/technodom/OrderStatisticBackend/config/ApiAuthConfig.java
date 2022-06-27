package com.technodom.OrderStatisticBackend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(value = "authenticationdb")
@Data
public class ApiAuthConfig {
    private String userName;
    private String password;
    private String url;

}
