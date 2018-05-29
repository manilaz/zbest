package com.zbest.queue.disruptor.demo3;

import com.lmax.disruptor.EventTranslator;

import java.util.Random;

/**
 * Created by zhangbin on 2018/5/24.
 */
public class TradeEventTranslator implements EventTranslator<TradeEvent> {

    private Random random=new Random();
    @Override
    public void translateTo(TradeEvent tradeEvent, long l) {

        tradeEvent.setId(""+random.nextDouble()*9999);

        tradeEvent.setPrice(random.nextDouble()*9999);

        System.out.println("translateTo..");
    }

}
