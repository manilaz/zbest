package com.zbest.thread.forkjoin;

import java.util.concurrent.RecursiveTask;

/**
 * Created by zhangbin on 2018/7/13.
 */
public class LogTask extends RecursiveTask<String> {

    private final String name;

    private final int second;

    public LogTask(String name, int second) {
        this.name = name;
        this.second = second;
    }

    @Override
    protected String compute() {

        System.out.println("LogTask-["+name+"]开始");

        try {
            Thread.sleep(1000 * 2 * second);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("LogTask-["+name+"]结束");

        return name;
    }
}
