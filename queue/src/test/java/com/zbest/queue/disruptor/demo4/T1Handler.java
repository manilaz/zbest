package com.zbest.queue.disruptor.demo4;

import com.lmax.disruptor.EventHandler;

/**
 * Created by zhangbin on 2018/5/29.
 */
public class T1Handler implements EventHandler<IntEvent> {

    @Override
    public void onEvent(IntEvent intEvent, long l, boolean b) throws Exception {
        System.out.println(intEvent);
    }
}
