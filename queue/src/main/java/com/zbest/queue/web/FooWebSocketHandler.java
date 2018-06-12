package com.zbest.queue.web;

import com.zbest.queue.bus.MapStore;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoProcessor;

import java.util.List;

/**
 * Created by zhangbin on 2018/6/7.
 */
@Component
public class FooWebSocketHandler implements WebSocketHandler {
    @Override
    public List<String> getSubProtocols() {
        return null;
    }


    @Override
    public Mono<Void> handle(WebSocketSession webSocketSession) {

//        Mono<WebSocketMessage>.just(,)
//        webSocketSession.send(new );


        System.out.println("webSocketSession:id:"+webSocketSession.getId());

        MapStore.add(webSocketSession.getId(),webSocketSession);

//        msg -> webSocketSession.textMessage("服务端返回：jack ->" + msg.getPayloadAsText());

//        Flux<WebSocketMessage> flux = webSocketSession.receive().map(new WebSocketMessage(WebSocketMessage.Type.TEXT,));

//        WebSocketMessage message = webSocketSession.textMessage("测试啊");
//        Mono<WebSocketMessage> just = Mono.just(message);
//        Mono<Void> send = webSocketSession.send(just);
//        return webSocketSession.send(webSocketSession.receive().
//                map(msg -> webSocketSession
//                        .textMessage("服务端返回：jack ->" + msg.getPayloadAsText())));

//        WebSocketMessage message = webSocketSession.textMessage("测试啊");
//        Mono<WebSocketMessage> just = Mono.just(message);
//
//        return webSocketSession.send(just);

        MonoProcessor<Object> output = MonoProcessor.create();

        return webSocketSession.receive().map(WebSocketMessage::getPayloadAsText)
                .subscribeWith(output).then();

    }
}
