package com.zbest.queue.bus;

/**
 * Created by zhangbin on 2018/6/30.
 */
public class TestTask implements Runnable {

    private String msg;

    @Override
    public void run() {
        System.out.println(msg);
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public TestTask(String msg){
        this.msg = msg;
    }
}
