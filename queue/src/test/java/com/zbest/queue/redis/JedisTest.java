package com.zbest.queue.redis;

import redis.clients.jedis.Jedis;

/**
 * Created by zhangbin on 2018/5/8.
 */
public class JedisTest {

    public static void main(String[] args) {


        Jedis jedis = new Jedis("47.92.4.77", 6379);

        jedis.set("foo","ssss");

        System.out.println(jedis.get("foo"));
    }
}
