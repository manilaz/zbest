package com.zbest.queue.disruptor;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WorkHandler;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ExceptionHandlerWrapper;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.concurrent.Executors;

/**
 * Created by zhangbin on 2018/5/19.
 */
public class Main1 {

    public static void main(String[] args) {


        Disruptor<LongEvent> disruptor = new Disruptor<>(new LongEventFactory()
                , 2 << 19, Executors.defaultThreadFactory(), ProducerType.MULTI
                , new BlockingWaitStrategy());

        RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();

        disruptor.setDefaultExceptionHandler(new ExceptionHandlerWrapper<>());


        WorkHandler<LongEvent> workHandler = new WorkHandler<LongEvent>() {

            @Override
            public void onEvent(LongEvent longEvent) throws Exception {
                System.out.println("ccccc");
            }
        };

        WorkHandler[] workHandlers = new WorkHandler[50];


        disruptor.handleEventsWithWorkerPool(workHandlers);

    }
}
