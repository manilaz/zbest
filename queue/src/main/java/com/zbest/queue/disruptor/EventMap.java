package com.zbest.queue.disruptor;

import java.util.Map;

/**
 * Created by zhangbin on 2018/6/5.
 */
public class EventMap {

    private Map<String,Object> map;

    private EventEnum type;

    public Map<String, Object> getMap() {
        return map;
    }

    public void setMap(Map<String, Object> map) {
        this.map = map;
    }

    public EventEnum getType() {
        return type;
    }

    public void setType(EventEnum type) {
        this.type = type;
    }
}
