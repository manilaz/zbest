package com.zbest.queue.disruptor.demo3;

/**
 * Created by zhangbin on 2018/5/24.
 */
public class TradeEvent {

    private String id; //交易ID
    private double price;//交易金额

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
