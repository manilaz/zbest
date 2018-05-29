package com.zbest.queue.disruptor.demo4;

import com.lmax.disruptor.EventFactory;

/**
 * Created by zhangbin on 2018/5/29.
 */
//RingBuffer中存储的单元
public class IntEvent {
    private int value = -1;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String toString() {
        return String.valueOf(value);
    }

    public static EventFactory<IntEvent> INT_ENEVT_FACTORY = new EventFactory<IntEvent>() {
        public IntEvent newInstance() {
            return new IntEvent();
        }
    };










}
