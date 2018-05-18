package com.zbest.queue.disruptor;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;

import java.nio.ByteBuffer;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by zhangbin on 2018/5/17.
 */
public class LongEventMain {

    public static void main(String[] args) {


    }

    public static void run(){
        // Executor that will be used to construct new threads for consumers
        Executor executor = Executors.newCachedThreadPool();
        // The factory for the event
        EventFactory<LongEvent> eventFactory = new LongEventFactory();
        // RingBuffer 大小，必须是 2 的 N 次方
        int ringBufferSize = 2<<19;
        // Construct the Disruptor
        Disruptor<LongEvent> disruptor = new Disruptor<LongEvent>(eventFactory, ringBufferSize
                , Executors.defaultThreadFactory(), ProducerType.MULTI,new BlockingWaitStrategy());
        EventHandler<LongEvent> eventHandler = new LongEventHandler();
        disruptor.handleEventsWith(eventHandler);//连接handler
        disruptor.start();//启动disruptor，启动所有线程
        //从disruptor中获得ringBuffer用于发布
        RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();

        ByteBuffer bb = ByteBuffer.allocate(8);

        LongEventProducer producer = new LongEventProducer(ringBuffer);

        LongEventProducerWithTranslator translator = new LongEventProducerWithTranslator(ringBuffer);

        for (long l = 0; true; l++) {
            bb.putLong(0, l);
            producer.onData(bb);
            //translator.onData(bb);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void t3() {

        // Executor that will be used to construct new threads for consumers
        Executor executor = Executors.newCachedThreadPool();
        // Specify the size of the ring buffer, must be power of 2.
        int bufferSize = 1024;// Construct the Disruptor
        Disruptor<LongEvent> disruptor = new Disruptor<>(LongEvent::new, bufferSize, executor);
        // 可以使用lambda来注册一个EventHandler
        disruptor.handleEventsWith((event, x, n) -> System.out.println("Event: " + event.getValue()));

        // Start the Disruptor, starts all threads running
        disruptor.start();
        // Get the ring buffer from the Disruptor to be used for publishing.
        RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();

        ByteBuffer bb = ByteBuffer.allocate(8);
        for (long l = 0; true; l++) {
            bb.putLong(0, l);
            ringBuffer.publishEvent((event, sequence, buffer) -> event.setValue(buffer.getLong(0)), bb);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
