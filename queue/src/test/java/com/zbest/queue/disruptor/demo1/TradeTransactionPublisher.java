package com.zbest.queue.disruptor.demo1;

import com.lmax.disruptor.dsl.Disruptor;

import java.util.concurrent.CountDownLatch;

/**
 * Created by zhangbin on 2018/5/18.
 */
public class TradeTransactionPublisher implements Runnable {

    Disruptor<TradeTransaction> disruptor;
    private CountDownLatch latch;
    private static int LOOP=10000000;//模拟一千万次交易的发生

    public TradeTransactionPublisher(CountDownLatch latch,Disruptor<TradeTransaction> disruptor){
        this.disruptor = disruptor;
        this.latch = latch;
    }
    @Override
    public void run() {
        TradeTransactionEventTranslator tradeTransloator=new TradeTransactionEventTranslator();
        for(int i=0;i<1;i++){
            disruptor.publishEvent(tradeTransloator);
        }
        latch.countDown();
    }


}
