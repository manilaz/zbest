package com.zbest.queue.web;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Created by zhangbin on 2018/6/7.
 */
@Component
public class BarWebSocketHandler implements WebSocketHandler {
    @Override
    public List<String> getSubProtocols() {
        return null;
    }

    @Override
    public Mono<Void> handle(WebSocketSession webSocketSession) {
        return null;
    }
}
