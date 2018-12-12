package com.zbest.raft.model.reactor.deleting;

import com.zbest.raft.model.reactor.event.EventHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhangbin on 2018/3/22.
 */
public class Dispatcher {

    final Map<EventType,EventHandler> eventHandlers = new HashMap<EventType,EventHandler>();

    final Selector selector;

    public Dispatcher(Selector selector) {
        this.selector = selector;
    }

    public void registEventHandler(EventType type , EventHandler handler){
        eventHandlers.put(type,handler);
    }

    public void removeEventHandler(EventType type){
        eventHandlers.remove(type);
    }

    public void handleEvents(){

    }

    private void dispatcher(){

        while (true){

//            List<Event> events = selector.select();
//            events.forEach(event->{
//                EventHandler eventHandler = eventHandlers.get(event.type);
//                eventHandler.handle(event);
//                return;
//            });
        }
    }

}
