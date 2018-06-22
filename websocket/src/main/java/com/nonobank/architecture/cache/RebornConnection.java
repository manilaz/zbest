package com.nonobank.architecture.cache;

import io.codis.jodis.RoundRobinJedisPool;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.lang.Nullable;
import redis.clients.jedis.Jedis;

/**
 * Created by zhangbin on 2018/6/15.
 */
public class RebornConnection extends JedisConnection {


    public RebornConnection(Jedis jedis, RoundRobinJedisPool rebornJedisPool, int dbIndex) {
        super(jedis, rebornJedisPool.currentThreadPool(), dbIndex);
    }

    protected RebornConnection(Jedis jedis, @Nullable RoundRobinJedisPool rebornJedisPool, int dbIndex, String clientName) {
        super(jedis, rebornJedisPool.currentThreadPool(), dbIndex, clientName);
    }

}
