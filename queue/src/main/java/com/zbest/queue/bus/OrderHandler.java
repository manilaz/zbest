package com.zbest.queue.bus;

import com.lmax.disruptor.WorkHandler;
import com.zbest.queue.disruptor.EventMap;

/**
 * Created by zhangbin on 2018/6/5.
 */
public class OrderHandler implements WorkHandler<EventMap> {

    @Override
    public void onEvent(EventMap eventMap) throws Exception {

        System.out.println(eventMap.getType()+":"+eventMap.getMap().size());
        //处理订单
        Thread.sleep(1000);
    }
}
