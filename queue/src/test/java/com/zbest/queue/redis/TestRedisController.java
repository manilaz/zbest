package com.zbest.queue.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by zhangbin on 2018/5/7.
 */
@RestController("testRedisController")
public class TestRedisController {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @RequestMapping("put")
    public String putV(String value){
        Boolean aBoolean = redisTemplate.hasKey(value);

        if(aBoolean){
            BoundValueOperations<String, String> valueOperations = redisTemplate.boundValueOps(value);
            return valueOperations.get();
        }

        BoundListOperations<String, String> list = redisTemplate.boundListOps("list");


        redisTemplate.opsForValue().set(value, value);


        return "";
    }

    public String pList(String list){

        return "";
    }

}
