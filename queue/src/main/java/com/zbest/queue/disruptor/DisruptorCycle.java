package com.zbest.queue.disruptor;

import com.lmax.disruptor.dsl.Disruptor;
import com.zbest.queue.bus.OrderHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * Created by zhangbin on 2018/6/7.
 */
@Component
public class DisruptorCycle {

    @Autowired
    private Disruptor<EventMap> disruptor;

    @Value("${disruptor.consumer.workSize}")
    private int workSize;

    @PreDestroy
    public void destory(){
        disruptor.shutdown();
    }

    @PostConstruct
    public void strart(){

        OrderHandler[] handlers = new OrderHandler[workSize];

        for (int i = 0; i < workSize; i++) {
            handlers[i] = new OrderHandler();
        }

        disruptor.handleEventsWithWorkerPool(handlers);

        disruptor.start();
    }
}
