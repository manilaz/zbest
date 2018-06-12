package com.zbest.queue.web;

import com.zbest.queue.bus.EventPublish;
import com.zbest.queue.bus.MapStore;
import com.zbest.queue.disruptor.EventEnum;
import com.zbest.queue.disruptor.EventMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by zhangbin on 2018/6/7.
 */
@Configuration
public class TestFlux {

    @Autowired
    private EventPublish eventPublish;

    @Bean
    public RouterFunction test(){

        return RouterFunctions.route(RequestPredicates.GET("/flux/test"), serverRequest ->{
            Optional<String> s = serverRequest.queryParam("s");
            Optional<String> a = serverRequest.queryParam("a");

            HashMap<String, Object> map = new HashMap<>();

            if(!StringUtils.isEmpty(s)){
                map.put("s",s);
            }

            if(!StringUtils.isEmpty(a)){
                map.put("a",a);
            }

            EventMap event = new EventMap();
            event.setMap(map);
            event.setType(EventEnum.ORDER);
            eventPublish.publish(event);

            return ServerResponse.ok().body(Mono.just("hi , webflux"),String.class);
        });
    }

    @Bean
    public HandlerMapping webSocketMapping() {
        Map<String, WebSocketHandler> map = new HashMap<>();
        map.put("/foo", new FooWebSocketHandler());
//        map.put("/bar", new BarWebSocketHandler());

        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setOrder(Ordered.HIGHEST_PRECEDENCE);
        mapping.setUrlMap(map);
        return mapping;
    }

    @Bean
    public WebSocketHandlerAdapter handlerAdapter() {
        return new WebSocketHandlerAdapter();
    }


    @Bean
    public RouterFunction testps(){

        return RouterFunctions.route(RequestPredicates.GET("/webflux/ps"), serverRequest -> {

            Optional<String> id = serverRequest.queryParam("id");

            WebSocketSession session = MapStore.sessions.get(id.get());



            WebSocketMessage message = session.textMessage("测试啊");
            session.receive().map(socketMessage->message);

            Mono<WebSocketMessage> just = Mono.just(message);
            session.send(just).then();

            return ServerResponse.ok().body(Mono.just("推送"+id+"成功"), String.class);
        });
    }
}
