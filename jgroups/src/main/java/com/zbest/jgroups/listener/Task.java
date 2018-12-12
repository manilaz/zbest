package com.zbest.jgroups.listener;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by zhangbin on 2018/7/2.
 */
public class Task<T extends Runnable> implements Delayed {

    private final long time;

    private final T task;

    private static final AtomicLong atomic = new AtomicLong(0);

    private final long n;

    public Task(long time, T task) {
        this.time = System.currentTimeMillis() + time;
        this.task = task;
        this.n = atomic.getAndIncrement();
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(this.time - System.currentTimeMillis(),unit);
    }

    @Override
    public int compareTo(Delayed other) {
        return (int)(this.getDelay(TimeUnit.MILLISECONDS) - other.getDelay(TimeUnit.MILLISECONDS));
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

    public long getTime() {
        return time;
    }

    public T getTask() {
        return task;
    }
}
