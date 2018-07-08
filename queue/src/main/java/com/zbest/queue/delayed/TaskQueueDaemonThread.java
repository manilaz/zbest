package com.zbest.queue.delayed;

import com.zbest.queue.bus.TestTask;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 *
 * 守护线程
 * Created by zhangbin on 2018/6/30.
 */
public class TaskQueueDaemonThread {

    private static final Logger logger = Logger.getLogger(TaskQueueDaemonThread.class.getName());

    /**
     * 创建一个最初为空的新 DelayQueue
     */
    private DelayQueue<Task> queue = new DelayQueue<>();

    private boolean run_flag = true;


    private static class LazyHolder {
        private static TaskQueueDaemonThread taskQueueDaemonThread = new TaskQueueDaemonThread();
    }

    public static TaskQueueDaemonThread getInstance() {
        return LazyHolder.taskQueueDaemonThread;
    }

    Executor executor = Executors.newFixedThreadPool(20);
    /**
     * 守护线程
     */
    private Thread daemonThread;

    /**
     * 初始化守护线程
     */
    public void init() {
        daemonThread = new Thread(() -> execute());
//        daemonThread.setDaemon(true);
        daemonThread.setName("Task Queue Daemon Thread");
        daemonThread.start();
    }

    public void destory(){
        Thread currentThread = Thread.currentThread();
        logger.info("销毁线程："+currentThread.getName());
        run_flag = false;
    }

    public void sou(String xx){

        System.out.println(xx);
    }

    /**
     * 启动
     */
    private void execute() {
        logger.info("start:" + System.currentTimeMillis());
        while (run_flag) {
            try {
                System.out.println("--------");
                //从延迟队列中取值,如果没有对象过期则队列一直等待，
                Task t1 = queue.take();
                if (t1 != null) {
                    //修改问题的状态
                    Runnable task = t1.getTask();
                    if (task == null) {
                        continue;
                    }
                    executor.execute(task);
                    logger.info("[at task:" + task + "]   [Time:" + System.currentTimeMillis() + "]");
                }
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }


    /**
     * 添加任务，
     * time 延迟时间
     * task 任务
     * 用户为问题设置延迟时间
     */
    public void put(long time, Runnable task) {
        //创建一个任务
        Task k = new Task(time, task);
        //将任务放在延迟的队列中
        queue.put(k);
    }

    /**
     * 结束订单
     * @param task
     */
    public boolean endTask(Task<Runnable> task){
        return queue.remove(task);
    }


}
