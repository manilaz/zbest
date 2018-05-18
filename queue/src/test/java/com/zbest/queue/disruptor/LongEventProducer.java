package com.zbest.queue.disruptor;

import com.lmax.disruptor.RingBuffer;

import java.nio.ByteBuffer;

/**
 * Created by zhangbin on 2018/5/17.
 */
public class LongEventProducer  {

    private final RingBuffer<LongEvent> ringBuffer;

    public LongEventProducer(RingBuffer<LongEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }


    public void onData(ByteBuffer bb) {
        long sequence = ringBuffer.next(); // Grab the next sequence
        try {
            LongEvent event = ringBuffer.get(sequence); // Get the entry in the Disruptor for the sequence
            event.setValue(bb.getLong(0)); // Fill with data
        } finally {
            ringBuffer.publish(sequence);
        }
    }

}
