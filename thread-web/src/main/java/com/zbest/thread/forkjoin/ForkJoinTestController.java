package com.zbest.thread.forkjoin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

/**
 * Created by zhangbin on 2018/7/13.
 */
@RestController
public class ForkJoinTestController {

    @Autowired
    private ForkJoinPool pool;

    @RequestMapping("testPrint")
    public String testPrint(String name,int second){

        PrintTask task = new PrintTask(name, second);

        PrintTask task2 = new PrintTask(name, second*3);

        //提交任务
        ForkJoinTask<Void> submit = pool.submit(task);
        ForkJoinTask<Void> submit2 = pool.submit(task2);

        submit.join();
        submit2.join();

        return name;
    }


    @RequestMapping("testLog")
    public String testLog(String name,int second){

        LogTask task = new LogTask(name, second);

        //提交任务
        ForkJoinTask<String> submit = pool.submit(task);


        try {
            Thread.sleep(20*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return "ssss";
    }
}
