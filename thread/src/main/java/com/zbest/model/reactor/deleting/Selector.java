package com.zbest.model.reactor.deleting;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by zhangbin on 2018/3/22.
 */
public class Selector {

    private BlockingQueue<Event> eventQueue = new LinkedBlockingQueue<Event>();

    private Object lock = new Object();


    List<Event> select(){
        return select(0);
    }

    //查询事件
    List<Event> select(long timeout){

        if (timeout > 0) {
            if (eventQueue.isEmpty()) {
                synchronized (lock) {
                    if (eventQueue.isEmpty()) {
                        try {
                            lock.wait(timeout);
                        } catch (InterruptedException e) {
                            // ignore it
                        }
                    }
                }

            }
        }
        List<Event> events = new ArrayList<Event>();
        eventQueue.drainTo(events);
        return events;
    }


    //添加事件
    public void addEvent(Event event){

        if(eventQueue.offer(event)){
            synchronized (lock){

                lock.notify();
            }

        }
    }





}
