package com.zbest.queue.disruptor.demo1;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;

/**
 * Created by zhangbin on 2018/5/18.
 */
public class T5Handler implements EventHandler<TradeTransaction>,WorkHandler<TradeTransaction> {

    @Override
    public void onEvent(TradeTransaction tradeTransaction, long l, boolean b) throws Exception {
        onEvent(tradeTransaction);
    }

    @Override
    public void onEvent(TradeTransaction tradeTransaction) throws Exception {

        System.out.println("T5Handler--------");
        Thread.sleep(3*1000);

    }
}
