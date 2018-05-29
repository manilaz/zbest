package com.zbest.queue.disruptor.demo4;

import com.lmax.disruptor.WorkHandler;

/**
 * Created by zhangbin on 2018/5/29.
 */
public //消费者
class IntEventProcessor implements WorkHandler<IntEvent> {

    public void onEvent(IntEvent event) throws Exception {
        System.out.println(event.getValue());
        event.setValue(1);
    }

}