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

        ExecutorService executor= Executors.newFixedThreadPool(120);

        Disruptor<TradeTransaction> disruptor=new Disruptor<TradeTransaction>(new EventFactory<TradeTransaction>() {
            @Override
            public TradeTransaction newInstance() {
                return new TradeTransaction();
            }
        }, bufferSize, executor, ProducerType.SINGLE, new BusySpinWaitStrategy());

        T1Handler t1 = new T1Handler();
        T2Handler t2 = new T2Handler();


        EventHandlerGroup<TradeTransaction> group = disruptor.handleEventsWith(t1,t2);

        disruptor.after(t2).then(new T3Handler());


        disruptor.after(t2).thenHandleEventsWithWorkerPool(new T4Handler());


        disruptor.start();
        CountDownLatch latch = new CountDownLatch(1);
        executor.submit(new TradeTransactionPublisher(latch, disruptor));
        latch.await();//等待生产者完事.
        disruptor.shutdown();
        executor.shutdown();

        System.out.println("总耗时:"+(System.currentTimeMillis()-beginTime));

    }
}
