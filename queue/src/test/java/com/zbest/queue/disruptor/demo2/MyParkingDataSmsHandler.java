package com.zbest.queue.disruptor.demo2;

import com.lmax.disruptor.EventHandler;

/**
 * Created by zhangbin on 2018/5/21.
 */
public class MyParkingDataSmsHandler implements EventHandler<MyInParkingDataEvent> {

    @Override
    public void onEvent(MyInParkingDataEvent myInParkingDataEvent, long sequence, boolean endOfBatch)
            throws Exception {
        long threadId = Thread.currentThread().getId(); // 获取当前线程id
        String carLicense = myInParkingDataEvent.getCarLicense(); // 获取车牌号
        System.out.println(String.format("Thread Id %s 给  %s 的车主发送一条短信，并告知他计费开始了 ....", threadId, carLicense));
    }
}
