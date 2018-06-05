package com.zbest.queue.web;

import com.zbest.queue.bus.EventPublish;
import com.zbest.queue.disruptor.EventEnum;
import com.zbest.queue.disruptor.EventMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

/**
 * Created by zhangbin on 2018/6/5.
 */
@RestController
public class TestRestController {

    @Autowired
    private EventPublish eventPublish;

    @RequestMapping("/home")
    String home(){

        return "hi,spring boot";
    }

    @RequestMapping("/create")
    String create(String s,String a){
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
        return "success";
    }

}
