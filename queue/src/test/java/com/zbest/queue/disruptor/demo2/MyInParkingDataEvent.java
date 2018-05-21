package com.zbest.queue.disruptor.demo2;

/**
 * Created by zhangbin on 2018/5/21.
 */
public class MyInParkingDataEvent {
    private String carLicense; // 车牌号

    public String getCarLicense() {
        return carLicense;
    }

    public void setCarLicense(String carLicense) {
        this.carLicense = carLicense;
    }
}
