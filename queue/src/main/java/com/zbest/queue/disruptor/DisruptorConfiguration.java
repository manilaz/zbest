package com.zbest.queue.disruptor;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by zhangbin on 2018/6/5.
 */
@Configuration
public class DisruptorConfiguration {

    Disruptor<EventMap> disruptor;

    @Value("${disruptor.buffSize}")
    private int buffSize;

    @Bean(name = "disruptor")
    public Disruptor<EventMap> getDisruptor(){

        disruptor = new Disruptor(new EventFactory<EventMap>() {
            @Override
            public EventMap newInstance() {
                return new EventMap();
            }
        },buffSize,DaemonThreadFactory.INSTANCE, ProducerType.MULTI,new BlockingWaitStrategy());

        return disruptor;
    }

}
