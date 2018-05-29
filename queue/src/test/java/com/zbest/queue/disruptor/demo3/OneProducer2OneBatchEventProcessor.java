package com.zbest.queue.disruptor.demo3;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zhangbin on 2018/5/24.
 */
public class OneProducer2OneBatchEventProcessor {


    public static void main(String[] args) throws Exception {
        long beginTime = System.currentTimeMillis();


        int bufferSize = 1024*4;

        ExecutorService executor = Executors.newFixedThreadPool(1024*2);

        TradeEventFactory factory = new TradeEventFactory();

        RingBuffer<TradeEvent> ringBuffer = RingBuffer.create(ProducerType.SINGLE, factory, bufferSize, new YieldingWaitStrategy());


        SequenceBarrier sequenceBarrier = ringBuffer.newBarrier();

        CountDownLatch downLatch = new CountDownLatch(1);

        Event1Handler handler1 = new Event1Handler(downLatch);

        BatchEventProcessor<TradeEvent> processor1 = new BatchEventProcessor<>(ringBuffer, sequenceBarrier, handler1);

        ringBuffer.addGatingSequences(processor1.getSequence());

        BatchEventProcessor<TradeEvent> processor = new BatchEventProcessor<>(ringBuffer, sequenceBarrier, handler1);


        executor.submit(processor);

        long end = 10000;


        for (int i = 0; i < end; i++) {

            long next = ringBuffer.next();


            ringBuffer.get(next).setId(String.valueOf(i));

            ringBuffer.publish(next);
        }



        downLatch.await();

//        boolean isClose= true;
//        while(isClose){
//            Thread.sleep(100);
//            if(executor.isTerminated()){
//                isClose= false;
//            }
//        }


//        executor.awaitTermination(1000, TimeUnit.NANOSECONDS);
//
        executor.shutdown();
        processor.halt();
        System.out.println("总耗时:" + (System.currentTimeMillis() - beginTime));
    }
}
