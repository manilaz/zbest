package com.nonobank.architecture.cache;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import com.google.common.base.Strings;
import com.nonobank.architecture.enumeration.CacheEnvironment;
import com.nonobank.architecture.enumeration.ListPosition;
import io.codis.jodis.JedisResourcePool;
import io.codis.jodis.RoundRobinJedisPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.SortingParams;
import redis.clients.jedis.Tuple;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


public class CacheClient implements AbstractCacheClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(CacheClient.class);
    private JedisResourcePool jodisPool = null;
    private JedisPool jedisPool = null;
    private CacheClient.ResourcePool resourcePool;
    private CacheConfig config;

    public CacheClient(CacheConfig config) {
        this.config = config;
        String codisType = config.getCodisType().trim().toLowerCase();
        this.resourcePool = new CacheClient.ResourcePool(codisType);
        if("zookeeper".equals(codisType)) {
            this.fresh(this.resourcePool);
        } else {
            if(!"haproxy".equals(codisType)) {
                throw new RuntimeException("not support CodisType " + codisType + ",must be one of \'zookeeper\' and \'haproxy\',\'zookeeper\' is default");
            }

            this.freshHa(this.resourcePool);
        }

    }

    private JedisPool freshHa(CacheClient.ResourcePool pool) {
        if(this.jedisPool == null) {
            Class var2 = CacheClient.class;
            synchronized(CacheClient.class) {
                if(this.jedisPool == null) {
                    this.jedisPool = new JedisPool(
                            this.config.CacheConfig2JedisPoolConfig(),
                            this.config.getCodisHaProxy().getHost(),
                            this.config.getCodisHaProxy().getPort(),
                            this.config.getConnectionTimeout(),
                            Strings.isNullOrEmpty(this.config.getPassword())?null:this.config.getPassword(),
                            this.config.getDatabase(),
                            (String)null);
                    LOGGER.info("redis client config,host:{},port:{},connectTimeout:{},readTimeout:{},password:{},database:{}", new Object[]{this.config.getCodisHaProxy().getHost(), Integer.valueOf(this.config.getCodisHaProxy().getPort()), Integer.valueOf(this.config.getConnectionTimeout()), Integer.valueOf(this.config.getReadTimeout()), this.config.getPassword(), Integer.valueOf(this.config.getDatabase())});
                }
            }
        }

        pool.setJedisPool(this.jedisPool);
        return this.jedisPool;
    }

    private JedisResourcePool fresh(CacheClient.ResourcePool pool) {
        if(this.jodisPool == null) {
            Class var2 = CacheClient.class;
            synchronized(CacheClient.class) {
                if(this.jodisPool == null) {
                    this.jodisPool = RoundRobinJedisPool.create().poolConfig(this.config.CacheConfig2JedisPoolConfig()).curatorClient(this.config.getZkAddressAndPort(), this.config.getZkSessionTimeOutMs()).zkProxyDir(this.config.getZkProxyDir()).connectionTimeoutMs(this.config.getConnectionTimeout()).soTimeoutMs(this.config.getReadTimeout()).password(Strings.isNullOrEmpty(this.config.getPassword())?null:this.config.getPassword()).database(this.config.getDatabase()).build();
                    LOGGER.info("codis client config,zkAddressAndPort:{},zkSessionTimeOutMs:{},zkProxyDir:{},connectTimeout:{},readTimeout:{},password:{},database:{}", new Object[]{this.config.getZkAddressAndPort(), Integer.valueOf(this.config.getZkSessionTimeOutMs()), this.config.getZkProxyDir(), Integer.valueOf(this.config.getConnectionTimeout()), Integer.valueOf(this.config.getReadTimeout()), this.config.getPassword(), Integer.valueOf(this.config.getDatabase())});
                }
            }
        }

        pool.setJodisPool(this.jodisPool);
        return this.jodisPool;
    }

    public String setExpireWithRetry(String key, int seconds, String value, int retryTimes, int sleepSeconds) {
        int cur_time = 0;

        while(cur_time < retryTimes) {
            try {
                Jedis var24 = this.jedisPool.getResource();
                Throwable e1 = null;

                String var9;
                try {
                    var9 = var24.setex(key, seconds, value);
                } catch (Throwable var21) {
                    e1 = var21;
                    throw var21;
                } finally {
                    if(var24 != null) {
                        if(e1 != null) {
                            try {
                                var24.close();
                            } catch (Throwable var20) {
                                e1.addSuppressed(var20);
                            }
                        } else {
                            var24.close();
                        }
                    }

                }

                return var9;
            } catch (Exception var23) {
                Exception e = var23;
                LOGGER.info("cannot get redis client,retry currentTime: " + cur_time + " Excepton: " + var23.toString());
                var23.printStackTrace();

                try {
                    if(cur_time == retryTimes - 1) {
                        throw new RuntimeException(e);
                    }

                    Thread.sleep((long)(sleepSeconds * 1000));
                } catch (InterruptedException var19) {
                    var23.printStackTrace();
                }

                ++cur_time;
            }
        }

        return null;
    }

    public Boolean setWithRetry(String key, String value, int retryTimes, int sleepSeconds) {
        int cur_time = 0;

        while(cur_time < retryTimes) {
            try {
                Jedis var23 = this.jedisPool.getResource();
                Throwable e1 = null;

                Boolean var8;
                try {
                    var8 = Boolean.valueOf(var23.set(key, value).equals("OK"));
                } catch (Throwable var20) {
                    e1 = var20;
                    throw var20;
                } finally {
                    if(var23 != null) {
                        if(e1 != null) {
                            try {
                                var23.close();
                            } catch (Throwable var19) {
                                e1.addSuppressed(var19);
                            }
                        } else {
                            var23.close();
                        }
                    }

                }

                return var8;
            } catch (Exception var22) {
                Exception e = var22;
                LOGGER.info("cannot get redis client,retry currentTime: " + cur_time + " Excepton: " + var22.toString());
                var22.printStackTrace();

                try {
                    if(cur_time == retryTimes - 1) {
                        throw new RuntimeException(e);
                    }

                    Thread.sleep((long)(sleepSeconds * 1000));
                } catch (InterruptedException var18) {
                    var22.printStackTrace();
                }

                ++cur_time;
            }
        }

        return null;
    }

    public Long delWithRetry(int retryTimes, int sleepSeconds, String... keys) {
        int cur_time = 0;

        while(cur_time < retryTimes) {
            try {
                Jedis var22 = this.jedisPool.getResource();
                Throwable e1 = null;

                Long var7;
                try {
                    var7 = var22.del(keys);
                } catch (Throwable var19) {
                    e1 = var19;
                    throw var19;
                } finally {
                    if(var22 != null) {
                        if(e1 != null) {
                            try {
                                var22.close();
                            } catch (Throwable var18) {
                                e1.addSuppressed(var18);
                            }
                        } else {
                            var22.close();
                        }
                    }

                }

                return var7;
            } catch (Exception var21) {
                Exception e = var21;
                LOGGER.info("cannot get redis client,retry currentTime: " + cur_time + " Excepton: " + var21.toString());
                var21.printStackTrace();

                try {
                    if(cur_time == retryTimes - 1) {
                        throw new RuntimeException(e);
                    }

                    Thread.sleep((long)(sleepSeconds * 1000));
                } catch (InterruptedException var17) {
                    var21.printStackTrace();
                }

                ++cur_time;
            }
        }

        return null;
    }

    public Boolean expireWithRetry(String key, int seconds, int retryTimes, int sleepSeconds) {
        int cur_time = 0;

        while(cur_time < retryTimes) {
            try {
                Jedis var23 = this.jedisPool.getResource();
                Throwable e1 = null;

                Boolean var8;
                try {
                    var8 = Boolean.valueOf(var23.expire(key, seconds).longValue() == 1L);
                } catch (Throwable var20) {
                    e1 = var20;
                    throw var20;
                } finally {
                    if(var23 != null) {
                        if(e1 != null) {
                            try {
                                var23.close();
                            } catch (Throwable var19) {
                                e1.addSuppressed(var19);
                            }
                        } else {
                            var23.close();
                        }
                    }

                }

                return var8;
            } catch (Exception var22) {
                Exception e = var22;
                LOGGER.info("cannot get redis client,retry currentTime: " + cur_time + " Excepton: " + var22.toString());
                var22.printStackTrace();

                try {
                    if(cur_time == retryTimes - 1) {
                        throw new RuntimeException(e);
                    }

                    Thread.sleep((long)(sleepSeconds * 1000));
                } catch (InterruptedException var18) {
                    var22.printStackTrace();
                }

                ++cur_time;
            }
        }

        return null;
    }

    public String getWithRetry(String key, int retryTimes, int sleepSeconds){
        int cur_time = 0;

        while(cur_time < retryTimes) {
            try {
                Jedis var22 = this.jedisPool.getResource();
                Throwable var6 = null;

                String var7;
                try {
                    var7 = var22.get(key);
                } catch (Throwable var19) {
                    var6 = var19;
                    throw var19;
                } finally {
                    if(var22 != null) {
                        if(var6 != null) {
                            try {
                                var22.close();
                            } catch (Throwable var18) {
                                var6.addSuppressed(var18);
                            }
                        } else {
                            var22.close();
                        }
                    }

                }

                return var7;
            } catch (Exception var21) {
                Exception e = var21;
                LOGGER.info("cannot get redis client,retry currentTime: " + cur_time + " Excepton: " + var21.toString());

                try {
                    if(cur_time == retryTimes - 1) {
                        throw new RuntimeException(e);
                    }

                    Thread.sleep((long)(sleepSeconds * 1000));
                } catch (InterruptedException var17) {

                }

                ++cur_time;
            }
        }

        return null;
    }

    public CacheClient.ResourcePool  getResourcePool(){
        return this.resourcePool;
    }

    public Boolean set(String key, String value) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var4 = null;

            Boolean var5;
            try {
                var5 = Boolean.valueOf(e.set(key, value).equals("OK"));
            } catch (Throwable var15) {
                var4 = var15;
                throw var15;
            } finally {
                if(e != null) {
                    if(var4 != null) {
                        try {
                            e.close();
                        } catch (Throwable var14) {
                            var4.addSuppressed(var14);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var5;
        } catch (Exception var17) {
            if(this.config.getDebug()) {
                LOGGER.info(var17.getMessage());
            }

            throw var17;
        }
    }

    public String get(String key) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var3 = null;

            String var4;
            try {
                var4 = e.get(key);
            } catch (Throwable var14) {
                var3 = var14;
                throw var14;
            } finally {
                if(e != null) {
                    if(var3 != null) {
                        try {
                            e.close();
                        } catch (Throwable var13) {
                            var3.addSuppressed(var13);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var4;
        } catch (Exception var16) {
            if(this.config.getDebug()) {
                LOGGER.info(var16.getMessage());
            }

            throw var16;
        }
    }

    public Long setnx(String key, String value) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var4 = null;

            Long var5;
            try {
                var5 = e.setnx(key, value);
            } catch (Throwable var15) {
                var4 = var15;
                throw var15;
            } finally {
                if(e != null) {
                    if(var4 != null) {
                        try {
                            e.close();
                        } catch (Throwable var14) {
                            var4.addSuppressed(var14);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var5;
        } catch (Exception var17) {
            if(this.config.getDebug()) {
                LOGGER.info(var17.getMessage());
            }

            throw var17;
        }
    }

    public String getSet(String key, String value) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var4 = null;

            String var5;
            try {
                var5 = e.getSet(key, value);
            } catch (Throwable var15) {
                var4 = var15;
                throw var15;
            } finally {
                if(e != null) {
                    if(var4 != null) {
                        try {
                            e.close();
                        } catch (Throwable var14) {
                            var4.addSuppressed(var14);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var5;
        } catch (Exception var17) {
            if(this.config.getDebug()) {
                LOGGER.info(var17.getMessage());
            }

            throw var17;
        }
    }

    public Boolean exists(String key) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var3 = null;

            Boolean var4;
            try {
                var4 = e.exists(key);
            } catch (Throwable var14) {
                var3 = var14;
                throw var14;
            } finally {
                if(e != null) {
                    if(var3 != null) {
                        try {
                            e.close();
                        } catch (Throwable var13) {
                            var3.addSuppressed(var13);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var4;
        } catch (Exception var16) {
            if(this.config.getDebug()) {
                LOGGER.info(var16.getMessage());
            }

            throw var16;
        }
    }

    public Boolean expire(String key, int seconds) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var4 = null;

            Boolean var5;
            try {
                var5 = Boolean.valueOf(e.expire(key, seconds).longValue() == 1L);
            } catch (Throwable var15) {
                var4 = var15;
                throw var15;
            } finally {
                if(e != null) {
                    if(var4 != null) {
                        try {
                            e.close();
                        } catch (Throwable var14) {
                            var4.addSuppressed(var14);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var5;
        } catch (Exception var17) {
            if(this.config.getDebug()) {
                LOGGER.info(var17.getMessage());
            }

            throw var17;
        }
    }

    public Long expireAt(String key, long unixTime) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var5 = null;

            Long var6;
            try {
                var6 = e.expireAt(key, unixTime);
            } catch (Throwable var16) {
                var5 = var16;
                throw var16;
            } finally {
                if(e != null) {
                    if(var5 != null) {
                        try {
                            e.close();
                        } catch (Throwable var15) {
                            var5.addSuppressed(var15);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var6;
        } catch (Exception var18) {
            if(this.config.getDebug()) {
                LOGGER.info(var18.getMessage());
            }

            throw var18;
        }
    }

    public Long ttl(String key) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var3 = null;

            Long var4;
            try {
                var4 = e.ttl(key);
            } catch (Throwable var14) {
                var3 = var14;
                throw var14;
            } finally {
                if(e != null) {
                    if(var3 != null) {
                        try {
                            e.close();
                        } catch (Throwable var13) {
                            var3.addSuppressed(var13);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var4;
        } catch (Exception var16) {
            if(this.config.getDebug()) {
                LOGGER.info(var16.getMessage());
            }

            throw var16;
        }
    }

    public Long append(String key, String value) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var4 = null;

            Long var5;
            try {
                var5 = e.append(key, value);
            } catch (Throwable var15) {
                var4 = var15;
                throw var15;
            } finally {
                if(e != null) {
                    if(var4 != null) {
                        try {
                            e.close();
                        } catch (Throwable var14) {
                            var4.addSuppressed(var14);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var5;
        } catch (Exception var17) {
            if(this.config.getDebug()) {
                LOGGER.info(var17.getMessage());
            }

            throw var17;
        }
    }

    public Long strlen(String key) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var3 = null;

            Long var4;
            try {
                var4 = e.strlen(key);
            } catch (Throwable var14) {
                var3 = var14;
                throw var14;
            } finally {
                if(e != null) {
                    if(var3 != null) {
                        try {
                            e.close();
                        } catch (Throwable var13) {
                            var3.addSuppressed(var13);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var4;
        } catch (Exception var16) {
            if(this.config.getDebug()) {
                LOGGER.info(var16.getMessage());
            }

            throw var16;
        }
    }

    public Long decr(String key) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var3 = null;

            Long var4;
            try {
                var4 = e.decr(key);
            } catch (Throwable var14) {
                var3 = var14;
                throw var14;
            } finally {
                if(e != null) {
                    if(var3 != null) {
                        try {
                            e.close();
                        } catch (Throwable var13) {
                            var3.addSuppressed(var13);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var4;
        } catch (Exception var16) {
            if(this.config.getDebug()) {
                LOGGER.info(var16.getMessage());
            }

            throw var16;
        }
    }

    public Long decrBy(String key, long integer) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var5 = null;

            Long var6;
            try {
                var6 = e.decrBy(key, integer);
            } catch (Throwable var16) {
                var5 = var16;
                throw var16;
            } finally {
                if(e != null) {
                    if(var5 != null) {
                        try {
                            e.close();
                        } catch (Throwable var15) {
                            var5.addSuppressed(var15);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var6;
        } catch (Exception var18) {
            if(this.config.getDebug()) {
                LOGGER.info(var18.getMessage());
            }

            throw var18;
        }
    }

    public Long del(String... keys) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var3 = null;

            Long var4;
            try {
                var4 = e.del(keys);
            } catch (Throwable var14) {
                var3 = var14;
                throw var14;
            } finally {
                if(e != null) {
                    if(var3 != null) {
                        try {
                            e.close();
                        } catch (Throwable var13) {
                            var3.addSuppressed(var13);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var4;
        } catch (Exception var16) {
            if(this.config.getDebug()) {
                LOGGER.info(var16.getMessage());
            }

            throw var16;
        }
    }

    public String getrange(String key, long startOffset, long endOffset) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var7 = null;

            String var8;
            try {
                var8 = e.getrange(key, startOffset, endOffset);
            } catch (Throwable var18) {
                var7 = var18;
                throw var18;
            } finally {
                if(e != null) {
                    if(var7 != null) {
                        try {
                            e.close();
                        } catch (Throwable var17) {
                            var7.addSuppressed(var17);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var8;
        } catch (Exception var20) {
            if(this.config.getDebug()) {
                LOGGER.info(var20.getMessage());
            }

            throw var20;
        }
    }

    public Long setrange(String key, long offset, String value) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var6 = null;

            Long var7;
            try {
                var7 = e.setrange(key, offset, value);
            } catch (Throwable var17) {
                var6 = var17;
                throw var17;
            } finally {
                if(e != null) {
                    if(var6 != null) {
                        try {
                            e.close();
                        } catch (Throwable var16) {
                            var6.addSuppressed(var16);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var7;
        } catch (Exception var19) {
            if(this.config.getDebug()) {
                LOGGER.info(var19.getMessage());
            }

            throw var19;
        }
    }

    public Long hset(String key, String field, String value) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var5 = null;

            Long var6;
            try {
                var6 = e.hset(key, field, value);
            } catch (Throwable var16) {
                var5 = var16;
                throw var16;
            } finally {
                if(e != null) {
                    if(var5 != null) {
                        try {
                            e.close();
                        } catch (Throwable var15) {
                            var5.addSuppressed(var15);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var6;
        } catch (Exception var18) {
            if(this.config.getDebug()) {
                LOGGER.info(var18.getMessage());
            }

            throw var18;
        }
    }

    public String hget(String key, String field) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var4 = null;

            String var5;
            try {
                var5 = e.hget(key, field);
            } catch (Throwable var15) {
                var4 = var15;
                throw var15;
            } finally {
                if(e != null) {
                    if(var4 != null) {
                        try {
                            e.close();
                        } catch (Throwable var14) {
                            var4.addSuppressed(var14);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var5;
        } catch (Exception var17) {
            if(this.config.getDebug()) {
                LOGGER.info(var17.getMessage());
            }

            throw var17;
        }
    }

    public Map<String, String> hgetAll(String key) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var3 = null;

            Map var4;
            try {
                var4 = e.hgetAll(key);
            } catch (Throwable var14) {
                var3 = var14;
                throw var14;
            } finally {
                if(e != null) {
                    if(var3 != null) {
                        try {
                            e.close();
                        } catch (Throwable var13) {
                            var3.addSuppressed(var13);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var4;
        } catch (Exception var16) {
            if(this.config.getDebug()) {
                LOGGER.info(var16.getMessage());
            }

            throw var16;
        }
    }

    public Set<String> hkeys(String key) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var3 = null;

            Set var4;
            try {
                var4 = e.hkeys(key);
            } catch (Throwable var14) {
                var3 = var14;
                throw var14;
            } finally {
                if(e != null) {
                    if(var3 != null) {
                        try {
                            e.close();
                        } catch (Throwable var13) {
                            var3.addSuppressed(var13);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var4;
        } catch (Exception var16) {
            if(this.config.getDebug()) {
                LOGGER.info(var16.getMessage());
            }

            throw var16;
        }
    }

    public Long hlen(String key) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var3 = null;

            Long var4;
            try {
                var4 = e.hlen(key);
            } catch (Throwable var14) {
                var3 = var14;
                throw var14;
            } finally {
                if(e != null) {
                    if(var3 != null) {
                        try {
                            e.close();
                        } catch (Throwable var13) {
                            var3.addSuppressed(var13);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var4;
        } catch (Exception var16) {
            if(this.config.getDebug()) {
                LOGGER.info(var16.getMessage());
            }

            throw var16;
        }
    }

    public List<String> hmget(String key, String... fields) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var4 = null;

            List var5;
            try {
                var5 = e.hmget(key, fields);
            } catch (Throwable var15) {
                var4 = var15;
                throw var15;
            } finally {
                if(e != null) {
                    if(var4 != null) {
                        try {
                            e.close();
                        } catch (Throwable var14) {
                            var4.addSuppressed(var14);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var5;
        } catch (Exception var17) {
            if(this.config.getDebug()) {
                LOGGER.info(var17.getMessage());
            }

            throw var17;
        }
    }

    public String hmset(String key, Map<String, String> hash) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var4 = null;

            String var5;
            try {
                var5 = e.hmset(key, hash);
            } catch (Throwable var15) {
                var4 = var15;
                throw var15;
            } finally {
                if(e != null) {
                    if(var4 != null) {
                        try {
                            e.close();
                        } catch (Throwable var14) {
                            var4.addSuppressed(var14);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var5;
        } catch (Exception var17) {
            if(this.config.getDebug()) {
                LOGGER.info(var17.getMessage());
            }

            throw var17;
        }
    }

    public List<String> mget(String... keys) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var3 = null;

            List var4;
            try {
                var4 = e.mget(keys);
            } catch (Throwable var14) {
                var3 = var14;
                throw var14;
            } finally {
                if(e != null) {
                    if(var3 != null) {
                        try {
                            e.close();
                        } catch (Throwable var13) {
                            var3.addSuppressed(var13);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var4;
        } catch (Exception var16) {
            if(this.config.getDebug()) {
                LOGGER.info(var16.getMessage());
            }

            throw var16;
        }
    }

    public Boolean mset(String... keysvalues) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var3 = null;

            Boolean var4;
            try {
                var4 = Boolean.valueOf(e.mset(keysvalues).equals("OK"));
            } catch (Throwable var14) {
                var3 = var14;
                throw var14;
            } finally {
                if(e != null) {
                    if(var3 != null) {
                        try {
                            e.close();
                        } catch (Throwable var13) {
                            var3.addSuppressed(var13);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var4;
        } catch (Exception var16) {
            if(this.config.getDebug()) {
                LOGGER.info(var16.getMessage());
            }

            throw var16;
        }
    }

    /** @deprecated */
    @Deprecated
    private String keyWapper(String key) {
        if(!Strings.isNullOrEmpty(key)) {
            key = CacheEnvironment.env(this.config.getEnvrionment()).encode(key);
            return key;
        } else {
            return null;
        }
    }

    public Long incr(String key) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var3 = null;

            Long var4;
            try {
                var4 = e.incr(key);
            } catch (Throwable var14) {
                var3 = var14;
                throw var14;
            } finally {
                if(e != null) {
                    if(var3 != null) {
                        try {
                            e.close();
                        } catch (Throwable var13) {
                            var3.addSuppressed(var13);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var4;
        } catch (Exception var16) {
            if(this.config.getDebug()) {
                LOGGER.info(var16.getMessage());
            }

            throw var16;
        }
    }

    public Long incrBy(String key, long integer) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var5 = null;

            Long var6;
            try {
                var6 = e.incrBy(key, integer);
            } catch (Throwable var16) {
                var5 = var16;
                throw var16;
            } finally {
                if(e != null) {
                    if(var5 != null) {
                        try {
                            e.close();
                        } catch (Throwable var15) {
                            var5.addSuppressed(var15);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var6;
        } catch (Exception var18) {
            if(this.config.getDebug()) {
                LOGGER.info(var18.getMessage());
            }

            throw var18;
        }
    }

    public Long hdel(String key, String... fields) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var4 = null;

            Long var5;
            try {
                var5 = e.hdel(key, fields);
            } catch (Throwable var15) {
                var4 = var15;
                throw var15;
            } finally {
                if(e != null) {
                    if(var4 != null) {
                        try {
                            e.close();
                        } catch (Throwable var14) {
                            var4.addSuppressed(var14);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var5;
        } catch (Exception var17) {
            if(this.config.getDebug()) {
                LOGGER.info(var17.getMessage());
            }

            throw var17;
        }
    }

    public Boolean hexists(String key, String field) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var4 = null;

            Boolean var5;
            try {
                var5 = e.hexists(key, field);
            } catch (Throwable var15) {
                var4 = var15;
                throw var15;
            } finally {
                if(e != null) {
                    if(var4 != null) {
                        try {
                            e.close();
                        } catch (Throwable var14) {
                            var4.addSuppressed(var14);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var5;
        } catch (Exception var17) {
            if(this.config.getDebug()) {
                LOGGER.info(var17.getMessage());
            }

            throw var17;
        }
    }

    public Long hincrBy(String key, String field, long value) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var6 = null;

            Long var7;
            try {
                var7 = e.hincrBy(key, field, value);
            } catch (Throwable var17) {
                var6 = var17;
                throw var17;
            } finally {
                if(e != null) {
                    if(var6 != null) {
                        try {
                            e.close();
                        } catch (Throwable var16) {
                            var6.addSuppressed(var16);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var7;
        } catch (Exception var19) {
            if(this.config.getDebug()) {
                LOGGER.info(var19.getMessage());
            }

            throw var19;
        }
    }

    public Long hsetnx(String key, String field, String value) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var5 = null;

            Long var6;
            try {
                var6 = e.hsetnx(key, field, value);
            } catch (Throwable var16) {
                var5 = var16;
                throw var16;
            } finally {
                if(e != null) {
                    if(var5 != null) {
                        try {
                            e.close();
                        } catch (Throwable var15) {
                            var5.addSuppressed(var15);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var6;
        } catch (Exception var18) {
            if(this.config.getDebug()) {
                LOGGER.info(var18.getMessage());
            }

            throw var18;
        }
    }

    public List<String> hvals(String key) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var3 = null;

            List var4;
            try {
                var4 = e.hvals(key);
            } catch (Throwable var14) {
                var3 = var14;
                throw var14;
            } finally {
                if(e != null) {
                    if(var3 != null) {
                        try {
                            e.close();
                        } catch (Throwable var13) {
                            var3.addSuppressed(var13);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var4;
        } catch (Exception var16) {
            if(this.config.getDebug()) {
                LOGGER.info(var16.getMessage());
            }

            throw var16;
        }
    }

    public String lindex(String key, long index) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var5 = null;

            String var6;
            try {
                var6 = e.lindex(key, index);
            } catch (Throwable var16) {
                var5 = var16;
                throw var16;
            } finally {
                if(e != null) {
                    if(var5 != null) {
                        try {
                            e.close();
                        } catch (Throwable var15) {
                            var5.addSuppressed(var15);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var6;
        } catch (Exception var18) {
            if(this.config.getDebug()) {
                LOGGER.info(var18.getMessage());
            }

            throw var18;
        }
    }

    public Long linsert(String key, ListPosition where, String pivot, String value) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var6 = null;

            Long var7;
            try {
                var7 = e.linsert(key, where.warp(), pivot, value);
            } catch (Throwable var17) {
                var6 = var17;
                throw var17;
            } finally {
                if(e != null) {
                    if(var6 != null) {
                        try {
                            e.close();
                        } catch (Throwable var16) {
                            var6.addSuppressed(var16);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var7;
        } catch (Exception var19) {
            if(this.config.getDebug()) {
                LOGGER.info(var19.getMessage());
            }

            throw var19;
        }
    }

    public Long llen(String key) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var3 = null;

            Long var4;
            try {
                var4 = e.llen(key);
            } catch (Throwable var14) {
                var3 = var14;
                throw var14;
            } finally {
                if(e != null) {
                    if(var3 != null) {
                        try {
                            e.close();
                        } catch (Throwable var13) {
                            var3.addSuppressed(var13);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var4;
        } catch (Exception var16) {
            if(this.config.getDebug()) {
                LOGGER.info(var16.getMessage());
            }

            throw var16;
        }
    }

    public String lpop(String key) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var3 = null;

            String var4;
            try {
                var4 = e.lpop(key);
            } catch (Throwable var14) {
                var3 = var14;
                throw var14;
            } finally {
                if(e != null) {
                    if(var3 != null) {
                        try {
                            e.close();
                        } catch (Throwable var13) {
                            var3.addSuppressed(var13);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var4;
        } catch (Exception var16) {
            if(this.config.getDebug()) {
                LOGGER.info(var16.getMessage());
            }

            throw var16;
        }
    }

    public Long lpush(String key, String... strings) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var4 = null;

            Long var5;
            try {
                var5 = e.lpush(key, strings);
            } catch (Throwable var15) {
                var4 = var15;
                throw var15;
            } finally {
                if(e != null) {
                    if(var4 != null) {
                        try {
                            e.close();
                        } catch (Throwable var14) {
                            var4.addSuppressed(var14);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var5;
        } catch (Exception var17) {
            if(this.config.getDebug()) {
                LOGGER.info(var17.getMessage());
            }

            throw var17;
        }
    }

    public Long lpushx(String key, String... string) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var4 = null;

            Long var5;
            try {
                var5 = e.lpushx(key, string);
            } catch (Throwable var15) {
                var4 = var15;
                throw var15;
            } finally {
                if(e != null) {
                    if(var4 != null) {
                        try {
                            e.close();
                        } catch (Throwable var14) {
                            var4.addSuppressed(var14);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var5;
        } catch (Exception var17) {
            if(this.config.getDebug()) {
                LOGGER.info(var17.getMessage());
            }

            throw var17;
        }
    }

    public List<String> lrange(String key, long start, long end) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var7 = null;

            List var8;
            try {
                var8 = e.lrange(key, start, end);
            } catch (Throwable var18) {
                var7 = var18;
                throw var18;
            } finally {
                if(e != null) {
                    if(var7 != null) {
                        try {
                            e.close();
                        } catch (Throwable var17) {
                            var7.addSuppressed(var17);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var8;
        } catch (Exception var20) {
            if(this.config.getDebug()) {
                LOGGER.info(var20.getMessage());
            }

            throw var20;
        }
    }

    public Long lrem(String key, long count, String value) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var6 = null;

            Long var7;
            try {
                var7 = e.lrem(key, count, value);
            } catch (Throwable var17) {
                var6 = var17;
                throw var17;
            } finally {
                if(e != null) {
                    if(var6 != null) {
                        try {
                            e.close();
                        } catch (Throwable var16) {
                            var6.addSuppressed(var16);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var7;
        } catch (Exception var19) {
            if(this.config.getDebug()) {
                LOGGER.info(var19.getMessage());
            }

            throw var19;
        }
    }

    public String ltrim(String key, long start, long end) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var7 = null;

            String var8;
            try {
                var8 = e.ltrim(key, start, end);
            } catch (Throwable var18) {
                var7 = var18;
                throw var18;
            } finally {
                if(e != null) {
                    if(var7 != null) {
                        try {
                            e.close();
                        } catch (Throwable var17) {
                            var7.addSuppressed(var17);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var8;
        } catch (Exception var20) {
            if(this.config.getDebug()) {
                LOGGER.info(var20.getMessage());
            }

            throw var20;
        }
    }

    public String lset(String key, long index, String value) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var6 = null;

            String var7;
            try {
                var7 = e.lset(key, index, value);
            } catch (Throwable var17) {
                var6 = var17;
                throw var17;
            } finally {
                if(e != null) {
                    if(var6 != null) {
                        try {
                            e.close();
                        } catch (Throwable var16) {
                            var6.addSuppressed(var16);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var7;
        } catch (Exception var19) {
            if(this.config.getDebug()) {
                LOGGER.info(var19.getMessage());
            }

            throw var19;
        }
    }

    public String rpop(String key) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var3 = null;

            String var4;
            try {
                var4 = e.rpop(key);
            } catch (Throwable var14) {
                var3 = var14;
                throw var14;
            } finally {
                if(e != null) {
                    if(var3 != null) {
                        try {
                            e.close();
                        } catch (Throwable var13) {
                            var3.addSuppressed(var13);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var4;
        } catch (Exception var16) {
            if(this.config.getDebug()) {
                LOGGER.info(var16.getMessage());
            }

            throw var16;
        }
    }

    public Long rpush(String key, String... strings) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var4 = null;

            Long var5;
            try {
                var5 = e.rpush(key, strings);
            } catch (Throwable var15) {
                var4 = var15;
                throw var15;
            } finally {
                if(e != null) {
                    if(var4 != null) {
                        try {
                            e.close();
                        } catch (Throwable var14) {
                            var4.addSuppressed(var14);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var5;
        } catch (Exception var17) {
            if(this.config.getDebug()) {
                LOGGER.info(var17.getMessage());
            }

            throw var17;
        }
    }

    public Long rpushx(String key, String string) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var4 = null;

            Long var5;
            try {
                var5 = e.rpushx(key, new String[]{string});
            } catch (Throwable var15) {
                var4 = var15;
                throw var15;
            } finally {
                if(e != null) {
                    if(var4 != null) {
                        try {
                            e.close();
                        } catch (Throwable var14) {
                            var4.addSuppressed(var14);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var5;
        } catch (Exception var17) {
            if(this.config.getDebug()) {
                LOGGER.info(var17.getMessage());
            }

            throw var17;
        }
    }

    public Long sadd(String key, String... members) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var4 = null;

            Long var5;
            try {
                var5 = e.sadd(key, members);
            } catch (Throwable var15) {
                var4 = var15;
                throw var15;
            } finally {
                if(e != null) {
                    if(var4 != null) {
                        try {
                            e.close();
                        } catch (Throwable var14) {
                            var4.addSuppressed(var14);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var5;
        } catch (Exception var17) {
            if(this.config.getDebug()) {
                LOGGER.info(var17.getMessage());
            }

            throw var17;
        }
    }

    public Long scard(String key) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var3 = null;

            Long var4;
            try {
                var4 = e.scard(key);
            } catch (Throwable var14) {
                var3 = var14;
                throw var14;
            } finally {
                if(e != null) {
                    if(var3 != null) {
                        try {
                            e.close();
                        } catch (Throwable var13) {
                            var3.addSuppressed(var13);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var4;
        } catch (Exception var16) {
            if(this.config.getDebug()) {
                LOGGER.info(var16.getMessage());
            }

            throw var16;
        }
    }

    public Set<String> smembers(String key) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var3 = null;

            Set var4;
            try {
                var4 = e.smembers(key);
            } catch (Throwable var14) {
                var3 = var14;
                throw var14;
            } finally {
                if(e != null) {
                    if(var3 != null) {
                        try {
                            e.close();
                        } catch (Throwable var13) {
                            var3.addSuppressed(var13);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var4;
        } catch (Exception var16) {
            if(this.config.getDebug()) {
                LOGGER.info(var16.getMessage());
            }

            throw var16;
        }
    }

    public String setExpire(String key, int seconds, String value) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var5 = null;

            String var6;
            try {
                var6 = e.setex(key, seconds, value);
            } catch (Throwable var16) {
                var5 = var16;
                throw var16;
            } finally {
                if(e != null) {
                    if(var5 != null) {
                        try {
                            e.close();
                        } catch (Throwable var15) {
                            var5.addSuppressed(var15);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var6;
        } catch (Exception var18) {
            if(this.config.getDebug()) {
                LOGGER.info(var18.getMessage());
            }

            throw var18;
        }
    }

    public String spop(String key) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var3 = null;

            String var4;
            try {
                var4 = e.spop(key);
            } catch (Throwable var14) {
                var3 = var14;
                throw var14;
            } finally {
                if(e != null) {
                    if(var3 != null) {
                        try {
                            e.close();
                        } catch (Throwable var13) {
                            var3.addSuppressed(var13);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var4;
        } catch (Exception var16) {
            if(this.config.getDebug()) {
                LOGGER.info(var16.getMessage());
            }

            throw var16;
        }
    }

    public Boolean sismember(String key, String member) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var4 = null;

            Boolean var5;
            try {
                var5 = e.sismember(key, member);
            } catch (Throwable var15) {
                var4 = var15;
                throw var15;
            } finally {
                if(e != null) {
                    if(var4 != null) {
                        try {
                            e.close();
                        } catch (Throwable var14) {
                            var4.addSuppressed(var14);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var5;
        } catch (Exception var17) {
            if(this.config.getDebug()) {
                LOGGER.info(var17.getMessage());
            }

            throw var17;
        }
    }

    public List<String> sort(String key) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var3 = null;

            List var4;
            try {
                var4 = e.sort(key);
            } catch (Throwable var14) {
                var3 = var14;
                throw var14;
            } finally {
                if(e != null) {
                    if(var3 != null) {
                        try {
                            e.close();
                        } catch (Throwable var13) {
                            var3.addSuppressed(var13);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var4;
        } catch (Exception var16) {
            if(this.config.getDebug()) {
                LOGGER.info(var16.getMessage());
            }

            throw var16;
        }
    }

    public List<String> sort(String key, SortingParams sortingParameters) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var4 = null;

            List var5;
            try {
                var5 = e.sort(key, sortingParameters);
            } catch (Throwable var15) {
                var4 = var15;
                throw var15;
            } finally {
                if(e != null) {
                    if(var4 != null) {
                        try {
                            e.close();
                        } catch (Throwable var14) {
                            var4.addSuppressed(var14);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var5;
        } catch (Exception var17) {
            if(this.config.getDebug()) {
                LOGGER.info(var17.getMessage());
            }

            throw var17;
        }
    }

    public Long sort(String key, SortingParams sortingParameters, String dstkey) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var5 = null;

            Long var6;
            try {
                var6 = e.sort(key, sortingParameters, dstkey);
            } catch (Throwable var16) {
                var5 = var16;
                throw var16;
            } finally {
                if(e != null) {
                    if(var5 != null) {
                        try {
                            e.close();
                        } catch (Throwable var15) {
                            var5.addSuppressed(var15);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var6;
        } catch (Exception var18) {
            if(this.config.getDebug()) {
                LOGGER.info(var18.getMessage());
            }

            throw var18;
        }
    }

    public Long sort(String key, String dstkey) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var4 = null;

            Long var5;
            try {
                var5 = e.sort(key, dstkey);
            } catch (Throwable var15) {
                var4 = var15;
                throw var15;
            } finally {
                if(e != null) {
                    if(var4 != null) {
                        try {
                            e.close();
                        } catch (Throwable var14) {
                            var4.addSuppressed(var14);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var5;
        } catch (Exception var17) {
            if(this.config.getDebug()) {
                LOGGER.info(var17.getMessage());
            }

            throw var17;
        }
    }

    public String srandmember(String key) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var3 = null;

            String var4;
            try {
                var4 = e.srandmember(key);
            } catch (Throwable var14) {
                var3 = var14;
                throw var14;
            } finally {
                if(e != null) {
                    if(var3 != null) {
                        try {
                            e.close();
                        } catch (Throwable var13) {
                            var3.addSuppressed(var13);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var4;
        } catch (Exception var16) {
            if(this.config.getDebug()) {
                LOGGER.info(var16.getMessage());
            }

            throw var16;
        }
    }

    public List<String> srandmember(String key, int count) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var4 = null;

            List var5;
            try {
                var5 = e.srandmember(key, count);
            } catch (Throwable var15) {
                var4 = var15;
                throw var15;
            } finally {
                if(e != null) {
                    if(var4 != null) {
                        try {
                            e.close();
                        } catch (Throwable var14) {
                            var4.addSuppressed(var14);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var5;
        } catch (Exception var17) {
            if(this.config.getDebug()) {
                LOGGER.info(var17.getMessage());
            }

            throw var17;
        }
    }

    public Long srem(String key, String... members) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var4 = null;

            Long var5;
            try {
                var5 = e.srem(key, members);
            } catch (Throwable var15) {
                var4 = var15;
                throw var15;
            } finally {
                if(e != null) {
                    if(var4 != null) {
                        try {
                            e.close();
                        } catch (Throwable var14) {
                            var4.addSuppressed(var14);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var5;
        } catch (Exception var17) {
            if(this.config.getDebug()) {
                LOGGER.info(var17.getMessage());
            }

            throw var17;
        }
    }

    /** @deprecated */
    @Deprecated
    public String substr(String key, int start, int end) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var5 = null;

            String var6;
            try {
                var6 = e.substr(key, start, end);
            } catch (Throwable var16) {
                var5 = var16;
                throw var16;
            } finally {
                if(e != null) {
                    if(var5 != null) {
                        try {
                            e.close();
                        } catch (Throwable var15) {
                            var5.addSuppressed(var15);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var6;
        } catch (Exception var18) {
            if(this.config.getDebug()) {
                LOGGER.info(var18.getMessage());
            }

            throw var18;
        }
    }

    public String type(String key) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var3 = null;

            String var4;
            try {
                var4 = e.type(key);
            } catch (Throwable var14) {
                var3 = var14;
                throw var14;
            } finally {
                if(e != null) {
                    if(var3 != null) {
                        try {
                            e.close();
                        } catch (Throwable var13) {
                            var3.addSuppressed(var13);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var4;
        } catch (Exception var16) {
            if(this.config.getDebug()) {
                LOGGER.info(var16.getMessage());
            }

            throw var16;
        }
    }

    public Long zadd(String key, double score, String member) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var6 = null;

            Long var7;
            try {
                var7 = e.zadd(key, score, member);
            } catch (Throwable var17) {
                var6 = var17;
                throw var17;
            } finally {
                if(e != null) {
                    if(var6 != null) {
                        try {
                            e.close();
                        } catch (Throwable var16) {
                            var6.addSuppressed(var16);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var7;
        } catch (Exception var19) {
            if(this.config.getDebug()) {
                LOGGER.info(var19.getMessage());
            }

            throw var19;
        }
    }

    public Long zadd(String key, Map<String, Double> scoreMembers) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var4 = null;

            Long var5;
            try {
                var5 = e.zadd(key, scoreMembers);
            } catch (Throwable var15) {
                var4 = var15;
                throw var15;
            } finally {
                if(e != null) {
                    if(var4 != null) {
                        try {
                            e.close();
                        } catch (Throwable var14) {
                            var4.addSuppressed(var14);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var5;
        } catch (Exception var17) {
            if(this.config.getDebug()) {
                LOGGER.info(var17.getMessage());
            }

            throw var17;
        }
    }

    public Long zcard(String key) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var3 = null;

            Long var4;
            try {
                var4 = e.zcard(key);
            } catch (Throwable var14) {
                var3 = var14;
                throw var14;
            } finally {
                if(e != null) {
                    if(var3 != null) {
                        try {
                            e.close();
                        } catch (Throwable var13) {
                            var3.addSuppressed(var13);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var4;
        } catch (Exception var16) {
            if(this.config.getDebug()) {
                LOGGER.info(var16.getMessage());
            }

            throw var16;
        }
    }

    public Long zcount(String key, double min, double max) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var7 = null;

            Long var8;
            try {
                var8 = e.zcount(key, min, max);
            } catch (Throwable var18) {
                var7 = var18;
                throw var18;
            } finally {
                if(e != null) {
                    if(var7 != null) {
                        try {
                            e.close();
                        } catch (Throwable var17) {
                            var7.addSuppressed(var17);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var8;
        } catch (Exception var20) {
            if(this.config.getDebug()) {
                LOGGER.info(var20.getMessage());
            }

            throw var20;
        }
    }

    public Long zcount(String key, String min, String max) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var5 = null;

            Long var6;
            try {
                var6 = e.zcount(key, min, max);
            } catch (Throwable var16) {
                var5 = var16;
                throw var16;
            } finally {
                if(e != null) {
                    if(var5 != null) {
                        try {
                            e.close();
                        } catch (Throwable var15) {
                            var5.addSuppressed(var15);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var6;
        } catch (Exception var18) {
            if(this.config.getDebug()) {
                LOGGER.info(var18.getMessage());
            }

            throw var18;
        }
    }

    public Double zincrby(String key, double score, String member) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var6 = null;

            Double var7;
            try {
                var7 = e.zincrby(key, score, member);
            } catch (Throwable var17) {
                var6 = var17;
                throw var17;
            } finally {
                if(e != null) {
                    if(var6 != null) {
                        try {
                            e.close();
                        } catch (Throwable var16) {
                            var6.addSuppressed(var16);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var7;
        } catch (Exception var19) {
            if(this.config.getDebug()) {
                LOGGER.info(var19.getMessage());
            }

            throw var19;
        }
    }

    public Set<String> zrange(String key, long start, long end) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var7 = null;

            Set var8;
            try {
                var8 = e.zrange(key, start, end);
            } catch (Throwable var18) {
                var7 = var18;
                throw var18;
            } finally {
                if(e != null) {
                    if(var7 != null) {
                        try {
                            e.close();
                        } catch (Throwable var17) {
                            var7.addSuppressed(var17);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var8;
        } catch (Exception var20) {
            if(this.config.getDebug()) {
                LOGGER.info(var20.getMessage());
            }

            throw var20;
        }
    }

    public Set<String> zrangeByScore(String key, double min, double max) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var7 = null;

            Set var8;
            try {
                var8 = e.zrangeByScore(key, min, max);
            } catch (Throwable var18) {
                var7 = var18;
                throw var18;
            } finally {
                if(e != null) {
                    if(var7 != null) {
                        try {
                            e.close();
                        } catch (Throwable var17) {
                            var7.addSuppressed(var17);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var8;
        } catch (Exception var20) {
            if(this.config.getDebug()) {
                LOGGER.info(var20.getMessage());
            }

            throw var20;
        }
    }

    public Set<String> zrangeByScore(String key, double min, double max, int offset, int count) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var9 = null;

            Set var10;
            try {
                var10 = e.zrangeByScore(key, min, max, offset, count);
            } catch (Throwable var20) {
                var9 = var20;
                throw var20;
            } finally {
                if(e != null) {
                    if(var9 != null) {
                        try {
                            e.close();
                        } catch (Throwable var19) {
                            var9.addSuppressed(var19);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var10;
        } catch (Exception var22) {
            if(this.config.getDebug()) {
                LOGGER.info(var22.getMessage());
            }

            throw var22;
        }
    }

    public Set<String> zrangeByScore(String key, String min, String max) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var5 = null;

            Set var6;
            try {
                var6 = e.zrangeByScore(key, min, max);
            } catch (Throwable var16) {
                var5 = var16;
                throw var16;
            } finally {
                if(e != null) {
                    if(var5 != null) {
                        try {
                            e.close();
                        } catch (Throwable var15) {
                            var5.addSuppressed(var15);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var6;
        } catch (Exception var18) {
            if(this.config.getDebug()) {
                LOGGER.info(var18.getMessage());
            }

            throw var18;
        }
    }

    public Set<String> zrangeByScore(String key, String min, String max, int offset, int count) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var7 = null;

            Set var8;
            try {
                var8 = e.zrangeByScore(key, min, max, offset, count);
            } catch (Throwable var18) {
                var7 = var18;
                throw var18;
            } finally {
                if(e != null) {
                    if(var7 != null) {
                        try {
                            e.close();
                        } catch (Throwable var17) {
                            var7.addSuppressed(var17);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var8;
        } catch (Exception var20) {
            if(this.config.getDebug()) {
                LOGGER.info(var20.getMessage());
            }

            throw var20;
        }
    }

    public Set<Tuple> zrangeByScoreWithScores(String key, double min, double max) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var7 = null;

            Set var8;
            try {
                var8 = e.zrangeByScoreWithScores(key, min, max);
            } catch (Throwable var18) {
                var7 = var18;
                throw var18;
            } finally {
                if(e != null) {
                    if(var7 != null) {
                        try {
                            e.close();
                        } catch (Throwable var17) {
                            var7.addSuppressed(var17);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var8;
        } catch (Exception var20) {
            if(this.config.getDebug()) {
                LOGGER.info(var20.getMessage());
            }

            throw var20;
        }
    }

    public Set<Tuple> zrangeByScoreWithScores(String key, double min, double max, int offset, int count) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var9 = null;

            Set var10;
            try {
                var10 = e.zrangeByScoreWithScores(key, min, max, offset, count);
            } catch (Throwable var20) {
                var9 = var20;
                throw var20;
            } finally {
                if(e != null) {
                    if(var9 != null) {
                        try {
                            e.close();
                        } catch (Throwable var19) {
                            var9.addSuppressed(var19);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var10;
        } catch (Exception var22) {
            if(this.config.getDebug()) {
                LOGGER.info(var22.getMessage());
            }

            throw var22;
        }
    }

    public Set<Tuple> zrangeByScoreWithScores(String key, String min, String max) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var5 = null;

            Set var6;
            try {
                var6 = e.zrangeByScoreWithScores(key, min, max);
            } catch (Throwable var16) {
                var5 = var16;
                throw var16;
            } finally {
                if(e != null) {
                    if(var5 != null) {
                        try {
                            e.close();
                        } catch (Throwable var15) {
                            var5.addSuppressed(var15);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var6;
        } catch (Exception var18) {
            if(this.config.getDebug()) {
                LOGGER.info(var18.getMessage());
            }

            throw var18;
        }
    }

    public Set<Tuple> zrangeByScoreWithScores(String key, String min, String max, int offset, int count) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var7 = null;

            Set var8;
            try {
                var8 = e.zrangeByScoreWithScores(key, min, max, offset, count);
            } catch (Throwable var18) {
                var7 = var18;
                throw var18;
            } finally {
                if(e != null) {
                    if(var7 != null) {
                        try {
                            e.close();
                        } catch (Throwable var17) {
                            var7.addSuppressed(var17);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var8;
        } catch (Exception var20) {
            if(this.config.getDebug()) {
                LOGGER.info(var20.getMessage());
            }

            throw var20;
        }
    }

    public Set<Tuple> zrangeWithScores(String key, long start, long end) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var7 = null;

            Set var8;
            try {
                var8 = e.zrangeWithScores(key, start, end);
            } catch (Throwable var18) {
                var7 = var18;
                throw var18;
            } finally {
                if(e != null) {
                    if(var7 != null) {
                        try {
                            e.close();
                        } catch (Throwable var17) {
                            var7.addSuppressed(var17);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var8;
        } catch (Exception var20) {
            if(this.config.getDebug()) {
                LOGGER.info(var20.getMessage());
            }

            throw var20;
        }
    }

    public Long zrank(String key, String member) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var4 = null;

            Long var5;
            try {
                var5 = e.zrank(key, member);
            } catch (Throwable var15) {
                var4 = var15;
                throw var15;
            } finally {
                if(e != null) {
                    if(var4 != null) {
                        try {
                            e.close();
                        } catch (Throwable var14) {
                            var4.addSuppressed(var14);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var5;
        } catch (Exception var17) {
            if(this.config.getDebug()) {
                LOGGER.info(var17.getMessage());
            }

            throw var17;
        }
    }

    public Long zrem(String key, String... members) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var4 = null;

            Long var5;
            try {
                var5 = e.zrem(key, members);
            } catch (Throwable var15) {
                var4 = var15;
                throw var15;
            } finally {
                if(e != null) {
                    if(var4 != null) {
                        try {
                            e.close();
                        } catch (Throwable var14) {
                            var4.addSuppressed(var14);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var5;
        } catch (Exception var17) {
            if(this.config.getDebug()) {
                LOGGER.info(var17.getMessage());
            }

            throw var17;
        }
    }

    public Long zremrangeByRank(String key, long start, long end) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var7 = null;

            Long var8;
            try {
                var8 = e.zremrangeByRank(key, start, end);
            } catch (Throwable var18) {
                var7 = var18;
                throw var18;
            } finally {
                if(e != null) {
                    if(var7 != null) {
                        try {
                            e.close();
                        } catch (Throwable var17) {
                            var7.addSuppressed(var17);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var8;
        } catch (Exception var20) {
            if(this.config.getDebug()) {
                LOGGER.info(var20.getMessage());
            }

            throw var20;
        }
    }

    public Long zremrangeByScore(String key, double start, double end) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var7 = null;

            Long var8;
            try {
                var8 = e.zremrangeByScore(key, start, end);
            } catch (Throwable var18) {
                var7 = var18;
                throw var18;
            } finally {
                if(e != null) {
                    if(var7 != null) {
                        try {
                            e.close();
                        } catch (Throwable var17) {
                            var7.addSuppressed(var17);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var8;
        } catch (Exception var20) {
            if(this.config.getDebug()) {
                LOGGER.info(var20.getMessage());
            }

            throw var20;
        }
    }

    public Long zremrangeByScore(String key, String start, String end) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var5 = null;

            Long var6;
            try {
                var6 = e.zremrangeByScore(key, start, end);
            } catch (Throwable var16) {
                var5 = var16;
                throw var16;
            } finally {
                if(e != null) {
                    if(var5 != null) {
                        try {
                            e.close();
                        } catch (Throwable var15) {
                            var5.addSuppressed(var15);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var6;
        } catch (Exception var18) {
            if(this.config.getDebug()) {
                LOGGER.info(var18.getMessage());
            }

            throw var18;
        }
    }

    public Set<String> zrevrange(String key, long start, long end) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var7 = null;

            Set var8;
            try {
                var8 = e.zrevrange(key, start, end);
            } catch (Throwable var18) {
                var7 = var18;
                throw var18;
            } finally {
                if(e != null) {
                    if(var7 != null) {
                        try {
                            e.close();
                        } catch (Throwable var17) {
                            var7.addSuppressed(var17);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var8;
        } catch (Exception var20) {
            if(this.config.getDebug()) {
                LOGGER.info(var20.getMessage());
            }

            throw var20;
        }
    }

    public Set<String> zrevrangeByScore(String key, double max, double min) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var7 = null;

            Set var8;
            try {
                var8 = e.zrevrangeByScore(key, max, min);
            } catch (Throwable var18) {
                var7 = var18;
                throw var18;
            } finally {
                if(e != null) {
                    if(var7 != null) {
                        try {
                            e.close();
                        } catch (Throwable var17) {
                            var7.addSuppressed(var17);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var8;
        } catch (Exception var20) {
            if(this.config.getDebug()) {
                LOGGER.info(var20.getMessage());
            }

            throw var20;
        }
    }

    public Set<String> zrevrangeByScore(String key, double max, double min, int offset, int count) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var9 = null;

            Set var10;
            try {
                var10 = e.zrevrangeByScore(key, max, min, offset, count);
            } catch (Throwable var20) {
                var9 = var20;
                throw var20;
            } finally {
                if(e != null) {
                    if(var9 != null) {
                        try {
                            e.close();
                        } catch (Throwable var19) {
                            var9.addSuppressed(var19);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var10;
        } catch (Exception var22) {
            if(this.config.getDebug()) {
                LOGGER.info(var22.getMessage());
            }

            throw var22;
        }
    }

    public Set<String> zrevrangeByScore(String key, String max, String min) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var5 = null;

            Set var6;
            try {
                var6 = e.zrevrangeByScore(key, max, min);
            } catch (Throwable var16) {
                var5 = var16;
                throw var16;
            } finally {
                if(e != null) {
                    if(var5 != null) {
                        try {
                            e.close();
                        } catch (Throwable var15) {
                            var5.addSuppressed(var15);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var6;
        } catch (Exception var18) {
            if(this.config.getDebug()) {
                LOGGER.info(var18.getMessage());
            }

            throw var18;
        }
    }

    public Set<String> zrevrangeByScore(String key, String max, String min, int offset, int count) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var7 = null;

            Set var8;
            try {
                var8 = e.zrevrangeByScore(key, max, min, offset, count);
            } catch (Throwable var18) {
                var7 = var18;
                throw var18;
            } finally {
                if(e != null) {
                    if(var7 != null) {
                        try {
                            e.close();
                        } catch (Throwable var17) {
                            var7.addSuppressed(var17);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var8;
        } catch (Exception var20) {
            if(this.config.getDebug()) {
                LOGGER.info(var20.getMessage());
            }

            throw var20;
        }
    }

    public Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var7 = null;

            Set var8;
            try {
                var8 = e.zrevrangeByScoreWithScores(key, max, min);
            } catch (Throwable var18) {
                var7 = var18;
                throw var18;
            } finally {
                if(e != null) {
                    if(var7 != null) {
                        try {
                            e.close();
                        } catch (Throwable var17) {
                            var7.addSuppressed(var17);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var8;
        } catch (Exception var20) {
            if(this.config.getDebug()) {
                LOGGER.info(var20.getMessage());
            }

            throw var20;
        }
    }

    public Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min, int offset, int count) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var9 = null;

            Set var10;
            try {
                var10 = e.zrevrangeByScoreWithScores(key, max, min, offset, count);
            } catch (Throwable var20) {
                var9 = var20;
                throw var20;
            } finally {
                if(e != null) {
                    if(var9 != null) {
                        try {
                            e.close();
                        } catch (Throwable var19) {
                            var9.addSuppressed(var19);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var10;
        } catch (Exception var22) {
            if(this.config.getDebug()) {
                LOGGER.info(var22.getMessage());
            }

            throw var22;
        }
    }

    public Set<Tuple> zrevrangeByScoreWithScores(String key, String max, String min) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var5 = null;

            Set var6;
            try {
                var6 = e.zrevrangeByScoreWithScores(key, max, min);
            } catch (Throwable var16) {
                var5 = var16;
                throw var16;
            } finally {
                if(e != null) {
                    if(var5 != null) {
                        try {
                            e.close();
                        } catch (Throwable var15) {
                            var5.addSuppressed(var15);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var6;
        } catch (Exception var18) {
            if(this.config.getDebug()) {
                LOGGER.info(var18.getMessage());
            }

            throw var18;
        }
    }

    public Set<Tuple> zrevrangeByScoreWithScores(String key, String max, String min, int offset, int count) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var7 = null;

            Set var8;
            try {
                var8 = e.zrevrangeByScoreWithScores(key, max, min, offset, count);
            } catch (Throwable var18) {
                var7 = var18;
                throw var18;
            } finally {
                if(e != null) {
                    if(var7 != null) {
                        try {
                            e.close();
                        } catch (Throwable var17) {
                            var7.addSuppressed(var17);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var8;
        } catch (Exception var20) {
            if(this.config.getDebug()) {
                LOGGER.info(var20.getMessage());
            }

            throw var20;
        }
    }

    public Set<Tuple> zrevrangeWithScores(String key, long start, long end) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var7 = null;

            Set var8;
            try {
                var8 = e.zrevrangeWithScores(key, start, end);
            } catch (Throwable var18) {
                var7 = var18;
                throw var18;
            } finally {
                if(e != null) {
                    if(var7 != null) {
                        try {
                            e.close();
                        } catch (Throwable var17) {
                            var7.addSuppressed(var17);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var8;
        } catch (Exception var20) {
            if(this.config.getDebug()) {
                LOGGER.info(var20.getMessage());
            }

            throw var20;
        }
    }

    public Long zrevrank(String key, String member) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var4 = null;

            Long var5;
            try {
                var5 = e.zrevrank(key, member);
            } catch (Throwable var15) {
                var4 = var15;
                throw var15;
            } finally {
                if(e != null) {
                    if(var4 != null) {
                        try {
                            e.close();
                        } catch (Throwable var14) {
                            var4.addSuppressed(var14);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var5;
        } catch (Exception var17) {
            if(this.config.getDebug()) {
                LOGGER.info(var17.getMessage());
            }

            throw var17;
        }
    }

    public Double zscore(String key, String member) {
        try {
            Jedis e = this.resourcePool.getResource();
            Throwable var4 = null;

            Double var5;
            try {
                var5 = e.zscore(key, member);
            } catch (Throwable var15) {
                var4 = var15;
                throw var15;
            } finally {
                if(e != null) {
                    if(var4 != null) {
                        try {
                            e.close();
                        } catch (Throwable var14) {
                            var4.addSuppressed(var14);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var5;
        } catch (Exception var17) {
            if(this.config.getDebug()) {
                LOGGER.info(var17.getMessage());
            }

            throw var17;
        }
    }

    public Boolean set(CacheKey key, String value) {
        return this.set(key.key(), value);
    }

    public String get(CacheKey key) {
        return this.get(key.key());
    }

    public Long setnx(CacheKey key, String value) {
        return this.setnx(key.key(), value);
    }

    public String getSet(CacheKey key, String value) {
        return this.getSet(key.key(), value);
    }

    public Boolean exists(CacheKey key) {
        return this.exists(key.key());
    }

    public Boolean expire(CacheKey key, int seconds) {
        return this.expire(key.key(), seconds);
    }

    public Long expireAt(CacheKey key, long unixTime) {
        return this.expireAt(key.key(), unixTime);
    }

    public Long append(CacheKey key, String value) {
        return this.append(key.key(), value);
    }

    public Long decr(CacheKey key) {
        return this.decr(key.key());
    }

    public Long decrBy(CacheKey key, long integer) {
        return this.decrBy(key.key(), integer);
    }

    public Long incr(CacheKey key) {
        return this.incr(key.key());
    }

    public Long incrBy(CacheKey key, long integer) {
        return this.incrBy(key.key(), integer);
    }

    public Long del(CacheKey... keys) {
        String[] _keys = new String[keys.length];

        for(int i = 0; i < _keys.length; ++i) {
            _keys[i] = keys[i].key();
        }

        return this.del(_keys);
    }

    public String getrange(CacheKey key, long startOffset, long endOffset) {
        return this.getrange(key.key(), startOffset, endOffset);
    }

    public String hget(CacheKey key, String field) {
        return this.hget(key.key(), field);
    }

    public Long hdel(CacheKey key, String... fields) {
        return this.hdel(key.key(), fields);
    }

    public Boolean hexists(CacheKey key, String field) {
        return this.hexists(key.key(), field);
    }

    public Map<String, String> hgetAll(CacheKey key) {
        return this.hgetAll(key.key());
    }

    public Long hincrBy(CacheKey key, String field, long value) {
        return this.hincrBy(key.key(), field, value);
    }

    public Set<String> hkeys(CacheKey key) {
        return this.hkeys(key.key());
    }

    public Long hlen(CacheKey key) {
        return this.hlen(key.key());
    }

    public List<String> hmget(CacheKey key, String... fields) {
        return this.hmget(key.key(), fields);
    }

    public String hmset(CacheKey key, Map<String, String> hash) {
        return this.hmset(key.key(), hash);
    }

    public Long hset(CacheKey key, String field, String value) {
        return this.hset(key.key(), field, value);
    }

    public Long hsetnx(CacheKey key, String field, String value) {
        return this.hsetnx(key.key(), field, value);
    }

    public List<String> hvals(CacheKey key) {
        return this.hvals(key.key());
    }

    public String lindex(CacheKey key, long index) {
        return this.lindex(key.key(), index);
    }

    public Long linsert(CacheKey key, ListPosition where, String pivot, String value) {
        return this.linsert(key.key(), where, pivot, value);
    }

    public List<String> mget(CacheKey... keys) {
        String[] _keys = new String[keys.length];

        for(int i = 0; i < _keys.length; ++i) {
            _keys[i] = keys[i].key();
        }

        return this.mget(_keys);
    }

    public Boolean mset(CacheKeyValue... keysvalues) {
        String[] kvs = CacheKeyValue.concat0(keysvalues);
        return this.mset(kvs);
    }

    public Long llen(CacheKey key) {
        return this.llen(key.key());
    }

    public String lpop(CacheKey key) {
        return this.lpop(key.key());
    }

    public Long lpush(CacheKey key, String... strings) {
        return this.lpush(key.key(), strings);
    }

    public Long lpushx(CacheKey key, String... strings) {
        return this.lpushx(key.key(), strings);
    }

    public List<String> lrange(CacheKey key, long start, long end) {
        return this.lrange(key.key(), start, end);
    }

    public Long lrem(CacheKey key, long count, String value) {
        return this.lrem(key.key(), count, value);
    }

    public String ltrim(CacheKey key, long start, long end) {
        return this.ltrim(key.key(), start, end);
    }

    public String lset(CacheKey key, long index, String value) {
        return this.lset(key.key(), index, value);
    }

    public String rpop(CacheKey key) {
        return this.rpop(key.key());
    }

    public Long rpush(CacheKey key, String... strings) {
        return this.rpush(key.key(), strings);
    }

    public Long rpushx(CacheKey key, String string) {
        return this.rpushx(key.key(), string);
    }

    public Long sadd(CacheKey key, String... members) {
        return this.sadd(key.key(), members);
    }

    public Long scard(CacheKey key) {
        return this.scard(key.key());
    }

    public Set<String> smembers(CacheKey key) {
        return this.smembers(key.key());
    }

    public String setExpire(CacheKey key, int seconds, String value) {
        return this.setExpire(key.key(), seconds, value);
    }

    public String spop(CacheKey key) {
        return this.spop(key.key());
    }

    public Long setrange(CacheKey key, long offset, String value) {
        return this.setrange(key.key(), offset, value);
    }

    public Boolean sismember(CacheKey key, String member) {
        return this.sismember(key.key(), member);
    }

    public Long strlen(CacheKey key) {
        return this.strlen(key.key());
    }

    public Long ttl(CacheKey key) {
        return this.ttl(key.key());
    }

    public List<String> sort(CacheKey key) {
        return this.sort(key.key());
    }

    public List<String> sort(CacheKey key, SortingParams sortingParameters) {
        return this.sort(key.key(), sortingParameters);
    }

    public Long sort(CacheKey key, SortingParams sortingParameters, String dstkey) {
        return this.sort(key.key(), sortingParameters, dstkey);
    }

    public Long sort(CacheKey key, String dstkey) {
        return this.sort(key.key(), dstkey);
    }

    public String srandmember(CacheKey key) {
        return this.srandmember(key.key());
    }

    public List<String> srandmember(CacheKey key, int count) {
        return this.srandmember(key.key(), count);
    }

    public Long srem(CacheKey key, String... members) {
        return this.srem(key.key(), members);
    }

    public String substr(CacheKey key, int start, int end) {
        return this.substr(key.key(), start, end);
    }

    public String type(CacheKey key) {
        return this.type(key.key());
    }

    public Long zadd(CacheKey key, double score, String member) {
        return this.zadd(key.key(), score, member);
    }

    public Long zadd(CacheKey key, Map<String, Double> scoreMembers) {
        return this.zadd(key.key(), scoreMembers);
    }

    public Long zcard(CacheKey key) {
        return this.zcard(key.key());
    }

    public Long zcount(CacheKey key, double min, double max) {
        return this.zcount(key.key(), min, max);
    }

    public Long zcount(CacheKey key, String min, String max) {
        return this.zcount(key.key(), min, max);
    }

    public Double zincrby(CacheKey key, double score, String member) {
        return this.zincrby(key.key(), score, member);
    }

    public Set<String> zrange(CacheKey key, long start, long end) {
        return this.zrange(key.key(), start, end);
    }

    public Set<String> zrangeByScore(CacheKey key, double min, double max) {
        return this.zrangeByScore(key.key(), min, max);
    }

    public Set<String> zrangeByScore(CacheKey key, double min, double max, int offset, int count) {
        return this.zrangeByScore(key.key(), min, max, offset, count);
    }

    public Set<String> zrangeByScore(CacheKey key, String min, String max) {
        return this.zrangeByScore(key.key(), min, max);
    }

    public Set<String> zrangeByScore(CacheKey key, String min, String max, int offset, int count) {
        return this.zrangeByScore(key.key(), min, max, offset, count);
    }

    public Set<Tuple> zrangeByScoreWithScores(CacheKey key, double min, double max) {
        return this.zrangeByScoreWithScores(key.key(), min, max);
    }

    public Set<Tuple> zrangeByScoreWithScores(CacheKey key, double min, double max, int offset, int count) {
        return this.zrangeByScoreWithScores(key.key(), min, max, offset, count);
    }

    public Set<Tuple> zrangeByScoreWithScores(CacheKey key, String min, String max) {
        return this.zrangeByScoreWithScores(key.key(), min, max);
    }

    public Set<Tuple> zrangeByScoreWithScores(CacheKey key, String min, String max, int offset, int count) {
        return this.zrangeByScoreWithScores(key.key(), min, max, offset, count);
    }

    public Set<Tuple> zrangeWithScores(CacheKey key, long start, long end) {
        return this.zrangeWithScores(key.key(), start, end);
    }

    public Long zrank(CacheKey key, String member) {
        return this.zrank(key.key(), member);
    }

    public Long zrem(CacheKey key, String... members) {
        return this.zrem(key.key(), members);
    }

    public Long zremrangeByRank(CacheKey key, long start, long end) {
        return this.zremrangeByRank(key.key(), start, end);
    }

    public Long zremrangeByScore(CacheKey key, double start, double end) {
        return this.zremrangeByScore(key.key(), start, end);
    }

    public Long zremrangeByScore(CacheKey key, String start, String end) {
        return this.zremrangeByScore(key.key(), start, end);
    }

    public Set<String> zrevrange(CacheKey key, long start, long end) {
        return this.zrevrange(key.key(), start, end);
    }

    public Set<String> zrevrangeByScore(CacheKey key, double max, double min) {
        return this.zrevrangeByScore(key.key(), max, min);
    }

    public Set<String> zrevrangeByScore(CacheKey key, double max, double min, int offset, int count) {
        return this.zrevrangeByScore(key.key(), max, min, offset, count);
    }

    public Set<String> zrevrangeByScore(CacheKey key, String max, String min) {
        return this.zrevrangeByScore(key.key(), max, min);
    }

    public Set<String> zrevrangeByScore(CacheKey key, String max, String min, int offset, int count) {
        return this.zrevrangeByScore(key.key(), max, min, offset, count);
    }

    public Set<Tuple> zrevrangeByScoreWithScores(CacheKey key, double max, double min) {
        return this.zrevrangeByScoreWithScores(key.key(), max, min);
    }

    public Set<Tuple> zrevrangeByScoreWithScores(CacheKey key, double max, double min, int offset, int count) {
        return this.zrevrangeByScoreWithScores(key.key(), max, min, offset, count);
    }

    public Set<Tuple> zrevrangeByScoreWithScores(CacheKey key, String max, String min) {
        return this.zrevrangeByScoreWithScores(key.key(), max, min);
    }

    public Set<Tuple> zrevrangeByScoreWithScores(CacheKey key, String max, String min, int offset, int count) {
        return this.zrevrangeByScoreWithScores(key.key(), max, min, offset, count);
    }

    public Set<Tuple> zrevrangeWithScores(CacheKey key, long start, long end) {
        return this.zrevrangeWithScores(key.key(), start, end);
    }

    public Long zrevrank(CacheKey key, String member) {
        return this.zrevrank(key.key(), member);
    }

    public Double zscore(CacheKey key, String member) {
        return this.zscore(key.key(), member);
    }

    public String setExpireWithRetry(CacheKey key, int seconds, String value, int retryTimes, int sleepSeconds) {
        return this.setExpireWithRetry(key.key(), seconds, value, retryTimes, sleepSeconds);
    }

    public Boolean setWithRetry(CacheKey key, String value, int retryTimes, int sleepSeconds) {
        return this.setWithRetry(key.key(), value, retryTimes, sleepSeconds);
    }

    public Long delWithRetry(int retryTimes, int sleepSeconds, CacheKey... keys) {
        String[] _keys = new String[keys.length];

        for(int i = 0; i < _keys.length; ++i) {
            _keys[i] = keys[i].key();
        }

        return this.delWithRetry(retryTimes, sleepSeconds, _keys);
    }

    public Boolean expireWithRetry(CacheKey key, int seconds, int retryTimes, int sleepSeconds) {
        return this.expireWithRetry(key.key(), seconds, retryTimes, sleepSeconds);
    }

    public String getWithRetry(CacheKey key, int retryTimes, int sleepSeconds) {
        return this.getWithRetry(key.key(), retryTimes, sleepSeconds);
    }

    class ResourcePool {
        private String codisType;
        private JedisResourcePool jodisPool;
        private JedisPool jedisPool;

        public ResourcePool(String codisType) {
            Objects.requireNonNull(codisType, "codisType is null");
            this.codisType = codisType;
        }

        public String getCodisType() {
            return this.codisType;
        }

        public void setCodisType(String codisType) {
            this.codisType = codisType;
        }

        public JedisResourcePool getJodisPool() {
            return this.jodisPool;
        }

        public void setJodisPool(JedisResourcePool jodisPool) {
            this.jodisPool = jodisPool;
        }

        public JedisPool getJedisPool() {
            return this.jedisPool;
        }

        public void setJedisPool(JedisPool jedisPool) {
            this.jedisPool = jedisPool;
        }

        public Jedis getResource() {
            return "zookeeper".equals(this.codisType)?this.jodisPool.getResource():("haproxy".equals(this.codisType)?this.jedisPool.getResource():null);
        }
    }
}
