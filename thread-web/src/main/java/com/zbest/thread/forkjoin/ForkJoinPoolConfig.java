package com.zbest.thread.forkjoin;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ForkJoinPool;

/**
 * Created by zhangbin on 2018/7/13.
 */
@Configuration
public class ForkJoinPoolConfig {

    private static int count = 10;

    @Bean
    public ForkJoinPool getForkJoinPool(){

        ForkJoinPool joinPool = new ForkJoinPool(count);

        return joinPool;
    }
}
