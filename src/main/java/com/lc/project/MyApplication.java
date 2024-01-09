package com.lc.project;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@MapperScan("com.Lc.project.mapper")
public class MyApplication {
    public static ConfigurableApplicationContext springContext;
    public static void main(String[] args) {
        springContext = SpringApplication.run(MyApplication.class, args);
    }

}
