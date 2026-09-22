package com.websiteroute;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WebSiteRouteApplication {
    public static void main(String[] args) { SpringApplication.run(WebSiteRouteApplication.class, args); }
}
