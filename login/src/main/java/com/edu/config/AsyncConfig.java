package com.edu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Created by WangSong
 * 实现功能：该项目的定时提醒任务
 * 使用说明：
 *          1.每一个任务都是在不同的线程中（能实现多个定时任务，同时运行。其中某一个定时任务挂掉，其余的正常运行。）
 *          2.在定时任务的类或者方法上添加 @Async
 */
@Configuration //表明该类是一个配置类
@EnableAsync    //开启异步事件的支持
public class AsyncConfig {
    /*
    此处成员变量应该使用@Value从配置中读取
    */
    private int corePoolSize = 10;
    private int maxPoolSize = 200;
    private int queueCapacity = 10;
    private int keepAliveSeconds = 30000;

    /**
     * 创建定时任务线程池
     * @return
     */
    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);//线程池维护线程的最少数量
        executor.setMaxPoolSize(maxPoolSize);////线程池维护线程的最大数量
        executor.setQueueCapacity(queueCapacity);//线程池所使用的缓冲队列
        executor.setKeepAliveSeconds(keepAliveSeconds);//线程池维护线程所允许的空闲时间
        executor.initialize();
        return executor;
    }

}
