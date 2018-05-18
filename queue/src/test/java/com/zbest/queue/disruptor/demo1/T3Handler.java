package com.zbest.queue.disruptor.demo1;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;

/**
 * Created by zhangbin on 2018/5/18.
 */
public class T3Handler implements EventHandler<TradeTransaction> ,WorkHandler<TradeTransaction>{

    @Override
    public void onEvent(TradeTransaction tradeTransaction, long l, boolean b) throws Exception {


        onEvent(tradeTransaction);
    }

    @Override
    public void onEvent(TradeTransaction tradeTransaction) throws Exception {
        System.out.println("t3:==");

        Thread.sleep(4*1000);
    }
}
