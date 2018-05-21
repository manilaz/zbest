package com.zbest.queue.disruptor.demo1;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.EventHandlerGroup;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zhangbin on 2018/5/18.
 */
public class Main {

    public static void main(String[] args) throws Exception {

        long beginTime = System.currentTimeMillis();


        int bufferSize = 1024;

        ExecutorService executor= Executors.newFixedThreadPool(1024);

        Disruptor<TradeTransaction> disruptor=new Disruptor<>(new EventFactory<TradeTransaction>() {
            @Override
            public TradeTransaction newInstance() {
                return new TradeTransaction();
            }
        }, bufferSize, executor, ProducerType.MULTI, new BlockingWaitStrategy());


        T1Handler t1 = new T1Handler();
        T2Handler t2 = new T2Handler();
        T3Handler t3 = new T3Handler();

        T1Handler[] t1Handlers = new T1Handler[256];
        T2Handler[] t2Handlers = new T2Handler[256];
        T3Handler[] t3Handlers = new T3Handler[256];

        for (int i =0 ; i< 256 - 1 ;i++){
            t1Handlers[i] = t1;
            t2Handlers[i] = t2;
            t3Handlers[i] = t3;
        }

        disruptor.handleEventsWith(new T3Handler());

        EventHandlerGroup<TradeTransaction> group = disruptor
                .handleEventsWithWorkerPool(t1Handlers)

                .handleEventsWithWorkerPool(t3Handlers);

        EventHandlerGroup<TradeTransaction> group2 = disruptor.handleEventsWithWorkerPool(t2Handlers);

        group.and(group2);
        disruptor.handleEventsWithWorkerPool();

//        new WorkerPool<TradeTransaction>(disruptor.getRingBuffer(),);

        disruptor.getRingBuffer().addGatingSequences();

        disruptor.after(t1).handleEventsWithWorkerPool(t2Handlers);
        disruptor.after(t1).handleEventsWithWorkerPool(t3Handlers);





        disruptor.start();
        CountDownLatch latch = new CountDownLatch(1);
        executor.submit(new TradeTransactionPublisher(latch, disruptor));
        latch.await();//等待生产者完事.
        disruptor.shutdown();
        executor.shutdown();

        System.out.println("总耗时:"+(System.currentTimeMillis()-beginTime));

    }
}
