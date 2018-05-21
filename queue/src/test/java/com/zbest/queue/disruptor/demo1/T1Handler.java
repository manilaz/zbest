package com.zbest.queue.disruptor.demo1;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;

import java.util.UUID;

/**
 * Created by zhangbin on 2018/5/18.
 */
public class T1Handler implements EventHandler<TradeTransaction>,WorkHandler<TradeTransaction> {

    @Override
    public void onEvent(TradeTransaction tradeTransaction, long l, boolean b) throws Exception {
        this.onEvent(tradeTransaction);
    }


    public void onEvent(TradeTransaction event) throws Exception {
        event.setId(UUID.randomUUID().toString());
        System.out.println(event.getId());

        Thread.sleep(10*1000);

    }
}
