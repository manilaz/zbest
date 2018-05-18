package com.zbest.queue.disruptor;

import com.lmax.disruptor.EventHandler;

/**
 * Created by zhangbin on 2018/5/17.
 */
public class LongEventHandler implements EventHandler<LongEvent> {

    @Override
    public void onEvent(LongEvent longEvent, long l, boolean b) throws Exception {
        System.out.println(longEvent.getValue());
    }
}
