package com.zbest.queue.disruptor.demo4;

import com.lmax.disruptor.BatchEventProcessor;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zhangbin on 2018/5/29.
 */
public class DisruptorTest {


    public static void main(String[] args) throws InterruptedException {

        int bufferSize = 1024;

        ExecutorService executor = Executors.newFixedThreadPool(1024*2);


        RingBuffer<IntEvent> ringBuffer = RingBuffer.create(ProducerType.SINGLE, IntEvent.INT_ENEVT_FACTORY, bufferSize, new YieldingWaitStrategy());

        SequenceBarrier sequenceBarrier = ringBuffer.newBarrier();


        BatchEventProcessor<IntEvent> processor = new BatchEventProcessor<>(ringBuffer, sequenceBarrier, new T1Handler());


        ringBuffer.addGatingSequences(processor.getSequence());

//        IntEventProducer[] producers = new IntEventProducer[1];
//        for (int i = 0; i < producers.length; i++) {
//            producers[i] = new IntEventProducer();
//        }
//        WorkerPool<IntEvent> crawler = new WorkerPool<IntEvent>(ringBuffer,
//                sequenceBarrier,
//                new IntEventExceptionHandler(),
//                producers);
////        SequenceBarrier sb = ringBuffer.newBarrier(crawler.getWorkerSequences());
////        IntEventProcessor[] processors = new IntEventProcessor[1];
////        for (int i = 0; i < processors.length; i++) {
////            processors[i] = new IntEventProcessor();
////        }
//
////        WorkerPool<IntEvent> applier = new WorkerPool<IntEvent>(ringBuffer,sb,
////                new IntEventExceptionHandler(),
////                processors);
//        List<Sequence> gatingSequences = new ArrayList<Sequence>();
//        for(Sequence s : crawler.getWorkerSequences()) {
//            gatingSequences.add(s);
//        }
////        for(Sequence s : applier.getWorkerSequences()) {
////            gatingSequences.add(s);
////        }
//        ringBuffer.addGatingSequences(gatingSequences.toArray(new Sequence[gatingSequences.size()]));
//        crawler.start(executor);
//        applier.start(executor);

        executor.submit(processor);

        int i = 0;
        while (true) {
            long lastSeq = ringBuffer.next();


            ringBuffer.get(lastSeq).setValue(i++);
            ringBuffer.publish(lastSeq);
        }
    }
}
