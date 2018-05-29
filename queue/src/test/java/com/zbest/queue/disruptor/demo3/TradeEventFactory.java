package com.zbest.queue.disruptor.demo3;


import com.lmax.disruptor.EventFactory;

/**
 * Created by zhangbin on 2018/5/24.
 */
public class TradeEventFactory implements EventFactory<TradeEvent> {

    @Override
    public TradeEvent newInstance() {

        return new TradeEvent();
    }
}
