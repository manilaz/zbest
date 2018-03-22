package com.zbest.model.reactor.event;

import com.zbest.model.reactor.deleting.Event;
import com.zbest.model.reactor.deleting.EventType;
import com.zbest.model.reactor.deleting.Selector;

/**
 * Created by zhangbin on 2018/3/22.
 */
public class AcceptEventHandler extends EventHandler {

    final Selector selector;

    public AcceptEventHandler(Selector selector) {
        this.selector = selector;
    }

    @Override
    public void handle(Event event) {
        System.out.println(this.getClass().toString());
        if (event.type == EventType.ACCEPT) {
            Event readEvent = new Event(EventType.READ, event.source);
            selector.addEvent(readEvent);
        }
    }
}
