package com.zbest.queue.disruptor.demo4;

import com.lmax.disruptor.ExceptionHandler;

/**
 * Created by zhangbin on 2018/5/29.
 */
public class IntEventExceptionHandler implements ExceptionHandler {
    public void handleEventException(Throwable ex, long sequence, Object event) {}
    public void handleOnStartException(Throwable ex) {}
    public void handleOnShutdownException(Throwable ex) {}
}