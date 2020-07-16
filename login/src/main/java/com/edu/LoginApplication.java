package com.edu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 系统模块启动入口
 */
@SpringBootApplication  //springboot项目启动注解（该注解可以优化，提升系统启动时间） ----21.366s
@EnableScheduling   //开启对定时任务的支持
@EnableTransactionManagement(proxyTargetClass = true) // 启注解事务管理，等同于xml配置方式的 <tx:annotation-driven />
@EnableAspectJAutoProxy(proxyTargetClass = true) //允许不同的代理模式（jdk/cglib）
//使用以下注解启动项目，提高项目启动性能   ----16.192s
//@EnableAutoConfiguration
//@Configuration
//@ComponentScan("com.edu")
@EnableEurekaClient //本服务注册到eureka注册中心
public class LoginApplication {
    public static void main( String[] args ) {
        SpringApplication.run(LoginApplication.class, args);
    }
}
