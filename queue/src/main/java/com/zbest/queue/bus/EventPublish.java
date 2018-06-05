package com.zbest.queue.bus;

import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.dsl.Disruptor;
import com.zbest.queue.disruptor.EventMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by zhangbin on 2018/6/5.
 */

@Component("eventPublish")
public class EventPublish {

    @Autowired
    private Disruptor<EventMap> disruptor;

    public void publish(EventMap event){

        disruptor.publishEvent(
                new EventTranslator<EventMap>() {
                                   @Override
                                   public void translateTo(EventMap eventMap, long sequence) {
                                       eventMap.setMap(event.getMap());
                                       eventMap.setType(event.getType());
                                   }
                               }
        );

    }

}
