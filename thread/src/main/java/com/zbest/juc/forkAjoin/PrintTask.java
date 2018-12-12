package com.zbest.juc.forkAjoin;

import java.util.concurrent.RecursiveAction;

/**
 * Created by zhangbin on 2018/7/13.
 */
public class PrintTask extends RecursiveAction {

    private final String name;

    private final int second;

    public PrintTask(String name,int second) {
        this.second = second;
        this.name = name;
    }

    @Override
    protected void compute() {
        System.out.printf("PrintTask-["+name+"]开始");

        try {
            Thread.sleep(1000 * second);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.printf("PrintTask-["+name+"]结束");
    }
}
