package com.wizard.ads;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Hello world!
 *
 */
@Slf4j
@SpringBootApplication
public class App
{
    public static void main( String[] args ){
        SpringApplication.run(App.class, args);
        log.info("服务启动");
    }
}
