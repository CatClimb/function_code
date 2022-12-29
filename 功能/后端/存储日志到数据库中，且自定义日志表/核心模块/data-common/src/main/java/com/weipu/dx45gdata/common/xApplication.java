package com.weipu.dx45gdata.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;



@Slf4j


@SpringBootApplication
public class xApplication {
    public static void main(String[] args) {
        SpringApplication.run(xApplication.class,args);
        log.info("ssdsd");
    }
}
