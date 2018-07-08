package com.zbest.queue.delayed;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhangbin on 2018/6/30.
 */
public class Task<T extends Runnable> implements Delayed {

    //延迟时间（单位毫秒）
    private final long time;
    //任务
    private final T task;

    public Task(long second,T t){
        this.time = System.currentTimeMillis() + second;
        this.task = t;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(this.time - System.currentTimeMillis(), unit);
    }

    @Override
    public int compareTo(Delayed o) {
        long td = this.getDelay(TimeUnit.MILLISECONDS);
        long od = o.getDelay(TimeUnit.MILLISECONDS);
        return td > od ? 1 : td == od ? 0 : -1;
    }

    public T getTask() {
        return task;
    }

    public long getTime() {
        return time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task<?> task1 = (Task<?>) o;

        return task.equals(task1.task);

    }

    @Override
    public int hashCode() {
        return task.hashCode();
    }
}
