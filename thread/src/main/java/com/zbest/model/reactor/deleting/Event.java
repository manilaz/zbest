package com.zbest.model.reactor.deleting;

/**
 * Created by zhangbin on 2018/3/22.
 */
public class Event {

    public final EventType type;

    public final Source source;

    public Event(EventType type, Source source) {
        this.type = type;
        this.source = source;
    }


}
