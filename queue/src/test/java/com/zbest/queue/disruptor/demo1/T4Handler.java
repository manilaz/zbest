package com.zbest.queue.disruptor.demo1;


import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;

/**
 * Created by zhangbin on 2018/5/18.
 */
public class T4Handler implements WorkHandler<TradeTransaction> ,EventHandler<TradeTransaction> {

    @Override
    public void onEvent(TradeTransaction tradeTransaction, long l, boolean b) throws Exception {
        onEvent(tradeTransaction);
    }

    @Override
    public void onEvent(TradeTransaction tradeTransaction) throws Exception {

        System.out.println("T4Handler--------");
        Thread.sleep(3*1000);

    }
}
