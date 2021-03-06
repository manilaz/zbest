package com.zbest.raft.model.reactor.event;

import com.zbest.raft.model.reactor.deleting.Event;
import com.zbest.raft.model.reactor.deleting.Source;

/**
 * Created by zhangbin on 2018/3/22.
 */
public abstract class EventHandler {

    protected Source source;

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public abstract void handle(Event event);
}
