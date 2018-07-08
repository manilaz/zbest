package com.zbest.queue.delayed;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * Created by zhangbin on 2018/6/30.
 */
@Component
public class DelayedQueueCycle {

    private final TaskQueueDaemonThread thread = TaskQueueDaemonThread.getInstance();

    @PreDestroy
    public void destory(){
        thread.destory();
    }

    @PostConstruct
    public void init(){
        thread.init();
    }
}
