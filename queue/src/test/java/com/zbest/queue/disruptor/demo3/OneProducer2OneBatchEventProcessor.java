package com.zbest.queue.disruptor.demo3;

import com.lmax.disruptor.BatchEventProcessor;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhangbin on 2018/5/24.
 */
public class OneProducer2OneBatchEventProcessor {


    public static void main(String[] args) throws Exception {
        long beginTime = System.currentTimeMillis();


        int bufferSize = 1024;

        ExecutorService executor = Executors.newFixedThreadPool(1024);

        TradeEventFactory factory = new TradeEventFactory();

        RingBuffer<TradeEvent> ringBuffer = RingBuffer.create(ProducerType.SINGLE, factory, bufferSize, new BlockingWaitStrategy());


        SequenceBarrier sequenceBarrier = ringBuffer.newBarrier();

        Event1Handler handler1 = new Event1Handler();

        BatchEventProcessor<TradeEvent> processor1 = new BatchEventProcessor<>(ringBuffer, sequenceBarrier, handler1);

        ringBuffer.addGatingSequences(processor1.getSequence());

        BatchEventProcessor<TradeEvent> processor = new BatchEventProcessor<>(ringBuffer, sequenceBarrier, handler1);


        executor.submit(processor);

        long end = 10;

        for (int i = 0; i < end; i++) {

            long next = ringBuffer.next();


            ringBuffer.get(next).setId(String.valueOf(i));

            ringBuffer.publish(next);
        }



        executor.shutdown();

        executor.awaitTermination(1000, TimeUnit.NANOSECONDS);

        processor.halt();




        boolean isClose= true;
        while(isClose){
            Thread.sleep(1);
            if(executor.isTerminated()){
                isClose= false;
            }
        }
        System.out.println("总耗时:" + (System.currentTimeMillis() - beginTime));
    }
}
