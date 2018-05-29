package com.zbest.queue.disruptor.demo3;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;

import java.util.concurrent.CountDownLatch;

/**
 * Created by zhangbin on 2018/5/24.
 */
public class Event1Handler implements WorkHandler<TradeEvent>,EventHandler<TradeEvent> {

    private CountDownLatch latch;

    public Event1Handler(){}

    public Event1Handler(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void onEvent(TradeEvent tradeEvent, long l, boolean b) throws Exception {

        onEvent(tradeEvent);

        if (latch != null)
        {
            latch.countDown();
        }
    }

    @Override
    public void onEvent(TradeEvent tradeEvent) throws Exception {

        //TODO do something
        System.out.println("Event1Handler:{}"+tradeEvent.getId());
//        Thread.sleep(1*1000);


    }
}
