package com.nonobank.architecture.cache;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.lang.reflect.Method;

/**
 * Created by zhangbin on 2018/6/13.
 */
@Configuration
public class CodisConfig {

    private String envrionment = "DEFAULT";

    @Value("${codis.debug}")
    private Boolean debug;

    @Value("${codis.pool.maxTotal}")
    private Integer maxTotal;

    @Value("${codis.pool.maxIdle}")
    private Integer maxIdle;

    @Value("${codis.pool.minIdle}")
    private Integer minIdle;

    @Value("${codis.pool.maxWaitMillis}")
    private Integer maxWaitMillis;

    private String codisType="haproxy";

    @Value("${codis.haproxy.haAddressAndPort}")
    private String haAddressAndPort;

    @Value("${codis.password}")
    private String password;


    @Bean("codisconfig")
    public CacheConfig getCacheConfig(){

        CacheConfig config = new CacheConfig();

        config.setCodisType(this.codisType);
        config.setEnvrionment(this.envrionment);
        config.setDebug(this.debug);
        config.setMaxTotal(this.maxTotal);
        config.setMaxIdle(this.maxIdle);
        config.setMinIdle(this.minIdle);
        config.setMaxWaitMillis(this.maxWaitMillis);
        config.setHaAddressAndPort(this.haAddressAndPort);
        config.setPassword(this.password);

        return config;
    }

    @Bean("cacheClient")
    public CacheClient getCacheClient(){
        return new CacheClient(getCacheConfig());
    }

    /**
     * @description 自定义的缓存key的生成策略
     *              若想使用这个key  只需要讲注解上keyGenerator的值设置为keyGenerator即可</br>
     * @return 自定义策略生成的key
     */
    @Bean
    public KeyGenerator keyGenerator() {
        return new KeyGenerator(){
            @Override
            public Object generate(Object target, Method method, Object... params) {
                StringBuffer sb = new StringBuffer();
                sb.append(target.getClass().getName());
                sb.append(method.getName());
                for(Object obj:params){
                    sb.append(obj.toString());
                }
                return sb.toString();
            }
        };
    }
    //缓存管理器
    @Bean
    public RedisCacheManager cacheManager(JedisConnectionFactory jedisConnectionFactory) {
        return RedisCacheManager.create(jedisConnectionFactory);
    }

    /**
     * RedisTemplate配置
     *
     * @param jedisConnectionFactory
     * @return
     */
    @Bean
//    @ConditionalOnBean(type = "RedisConnectionFactory.class")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory jedisConnectionFactory ) {
        //设置序列化
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        //配置redisTemplate
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory);
        RedisSerializer stringSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringSerializer);//key序列化
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);//value序列化
        redisTemplate.setHashKeySerializer(stringSerializer);//Hash key序列化
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);//Hash value序列化
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

//
//    @Bean
//    public RedisConnectionFactory redisConnectionFactory() {
//        return new RebornConnectionFactory(roundRobinJedisPool());
//    }
//
//    @Bean
//    public RoundRobinJedisPool roundRobinJedisPool(){
//
//        return RoundRobinJedisPool.create()
//                .poolConfig(getCacheConfig().CacheConfig2JedisPoolConfig()).build();
//    }


}
