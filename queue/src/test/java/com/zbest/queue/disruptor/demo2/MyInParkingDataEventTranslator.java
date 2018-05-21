package com.zbest.queue.disruptor.demo2;

import com.lmax.disruptor.EventTranslator;


/**
 * Created by zhangbin on 2018/5/21.
 */
public class MyInParkingDataEventTranslator implements EventTranslator<MyInParkingDataEvent> {

    @Override
    public void translateTo(MyInParkingDataEvent myInParkingDataEvent, long sequence) {
        this.generateData(myInParkingDataEvent);
    }

    private MyInParkingDataEvent generateData(MyInParkingDataEvent myInParkingDataEvent) {
        myInParkingDataEvent.setCarLicense("车牌号： 鄂A-" + (int)(Math.random() * 100000)); // 随机生成一个车牌号
        System.out.println("Thread Id " + Thread.currentThread().getId() + " 写完一个event");
        return myInParkingDataEvent;
    }
}
