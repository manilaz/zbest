package com.zbest.queue.disruptor.demo4;

import com.lmax.disruptor.WorkHandler;

/**
 * Created by zhangbin on 2018/5/29.
 */
public //生产者
class IntEventProducer implements WorkHandler<IntEvent> {

    private int seq = 0;
    public void onEvent(IntEvent event) throws Exception {
        System.out.println("produced " + seq);
        event.setValue(++seq);
    }

}