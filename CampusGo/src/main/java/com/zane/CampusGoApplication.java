package com.zane;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.zane.mapper")
@SpringBootApplication
public class CampusGoApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampusGoApplication.class, args);
    }

}
