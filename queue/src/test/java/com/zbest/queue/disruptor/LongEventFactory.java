package com.zbest.queue.disruptor;

import com.lmax.disruptor.EventFactory;

/**
 * Created by zhangbin on 2018/5/17.
 */
public class LongEventFactory implements EventFactory<LongEvent> {

    @Override
    public LongEvent newInstance() {
        return new LongEvent();
    }
}
