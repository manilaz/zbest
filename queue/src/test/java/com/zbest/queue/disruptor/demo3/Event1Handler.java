package com.zbest.queue.disruptor.demo3;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;

/**
 * Created by zhangbin on 2018/5/24.
 */
public class Event1Handler implements WorkHandler<TradeEvent>,EventHandler<TradeEvent> {

    @Override
    public void onEvent(TradeEvent tradeEvent, long l, boolean b) throws Exception {

        onEvent(tradeEvent);
    }

    @Override
    public void onEvent(TradeEvent tradeEvent) throws Exception {

        System.out.println("Event1Handler:{}"+tradeEvent.getId());
        Thread.sleep(1*1000);
    }
}
