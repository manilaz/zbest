package com.zbest.queue.disruptor;

import com.lmax.disruptor.dsl.Disruptor;
import com.zbest.queue.bus.OrderHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Created by zhangbin on 2018/6/5.
 */

@WebListener
public class DisruptorListener implements ServletContextListener{

    @Autowired
    private Disruptor<EventMap> disruptor;

    @Value("${disruptor.consumer.workSize}")
    private int workSize;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {

        OrderHandler[] handlers = new OrderHandler[workSize];

        for (int i = 0; i < workSize; i++) {
            handlers[i] = new OrderHandler();
        }

        disruptor.handleEventsWithWorkerPool(handlers);

        disruptor.start();

        System.out.println("disruptor。。start");

    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        disruptor.shutdown();
    }
}
