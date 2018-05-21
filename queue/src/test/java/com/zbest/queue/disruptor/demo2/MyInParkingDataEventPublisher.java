package com.zbest.queue.disruptor.demo2;

import com.lmax.disruptor.dsl.Disruptor;

import java.util.concurrent.CountDownLatch;

/**
 * Created by zhangbin on 2018/5/21.
 */
public class MyInParkingDataEventPublisher implements Runnable {

    private CountDownLatch countDownLatch; // 用于监听初始化操作，等初始化执行完毕后，通知主线程继续工作
    private Disruptor<MyInParkingDataEvent> disruptor;
    private static final Integer NUM = 10; // 1,10,100,1000

    public MyInParkingDataEventPublisher(CountDownLatch countDownLatch,
                                         Disruptor<MyInParkingDataEvent> disruptor) {
        this.countDownLatch = countDownLatch;
        this.disruptor = disruptor;
    }

    @Override
    public void run() {
        MyInParkingDataEventTranslator eventTranslator = new MyInParkingDataEventTranslator();
        try {
            for(int i = 0; i < NUM; i ++) {
                disruptor.publishEvent(eventTranslator);
                Thread.sleep(1000); // 假设一秒钟进一辆车
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            countDownLatch.countDown(); // 执行完毕后通知 await()方法
            System.out.println(NUM + "辆车已经全部进入进入停车场！");
        }
    }
}
