package com.zbest.queue.disruptor.demo3;

import com.lmax.disruptor.RingBuffer;

import java.util.concurrent.CountDownLatch;

/**
 * Created by zhangbin on 2018/5/24.
 */
public class TradePublisher implements Runnable {

    RingBuffer<TradeEvent> ringBuffer;
    private CountDownLatch latch;
    private static int LOOP=10;//模拟一千万次交易的发生

    public TradePublisher(CountDownLatch latch,RingBuffer<TradeEvent> ringBuffer){
        this.ringBuffer = ringBuffer;
        this.latch = latch;
    }
    @Override
    public void run() {
        TradeEventTranslator tradeTransloator=new TradeEventTranslator();
        long next = 0 ;
        long h = 0;
        for(int i=0;i<LOOP;i++){
//            ringBuffer.publishEvent(tradeTransloator);

                next = ringBuffer.next();
                h = next+1;
        }
        ringBuffer.publish(next,h);
        latch.countDown();
    }

}
