package com.zbest.websocket.web;

import com.nonobank.architecture.cache.CacheClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by zhangbin on 2018/6/13.
 */

@RestController
@RequestMapping("/redis")
public class RedisTestController {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private CacheClient client;

    @RequestMapping("/put")
    public Object put(String key,String v){

        redisTemplate.opsForValue().set(key,v);

        Object o = redisTemplate.opsForValue().get(key);

        return o;
    }

    @RequestMapping("/put2")
    public String put2(String key,String v){
        Boolean aBoolean = client.set(key, v);

        String s = client.get(key);


        return s;

    }

}
