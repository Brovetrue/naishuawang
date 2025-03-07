package com.chiu.naishuawangnextbackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@MapperScan("com.chiu.naishuawangnextbackend.mapper")
@EnableAspectJAutoProxy(exposeProxy = true)//代理开启
public class NaishuawangNextBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(NaishuawangNextBackendApplication.class, args);
    }

}
