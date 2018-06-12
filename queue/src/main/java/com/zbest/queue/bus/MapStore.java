package com.zbest.queue.bus;

import org.springframework.web.reactive.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhangbin on 2018/6/7.
 */
public class MapStore {

    public static final Map<String,WebSocketSession> sessions = new HashMap<>();

    public static Map<String,WebSocketSession> add(String id, WebSocketSession session){

        sessions.put(id, session);
        return sessions;
    }

}
