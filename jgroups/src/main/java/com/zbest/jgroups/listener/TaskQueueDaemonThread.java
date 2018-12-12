package com.zbest.jgroups.listener;

import org.jboss.logging.Logger;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by zhangbin on 2018/7/2.
 */
public class TaskQueueDaemonThread {

    private static final Logger logger = Logger.getLogger(TaskQueueDaemonThread.class);

    private TaskQueueDaemonThread() {
    }

    private static class LazyHolder {
        private static TaskQueueDaemonThread taskQueueDaemonThread = new TaskQueueDaemonThread();
    }

    public static TaskQueueDaemonThread getInstance() {
        return LazyHolder.taskQueueDaemonThread;
    }

    Executor executor = Executors.newFixedThreadPool(1000);

    /**
     * 守护线程
     */
    private Thread daemonThread;

    /**
     * 初始化守护线程
     */
    public void init() {
        daemonThread = new Thread(() -> execute());
        daemonThread.setDaemon(true);
        daemonThread.setName("Task Queue Daemon Thread");
        daemonThread.start();
    }

    private void execute() {
        System.out.println("start:" + System.currentTimeMillis());
        while (true) {
            try {
                //从延迟队列中取值,如果没有对象过期则队列一直等待，
                Task t1 = task.take();
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
                logger.debug("xxxxxxxxxxxxxxxxx===========x=======");
                e.printStackTrace();
                break;
            }
        }
    }

    /**
     * 创建一个最初为空的新 DelayQueue
     */
    private DelayQueue<Task> task = new DelayQueue<>();

    /**
     * 添加任务，
     * time 延迟时间
     * taskR 任务
     * 用户为问题设置延迟时间
     */
    public void put(long time, Runnable taskR) {
        //创建一个任务
        Task k = new Task(time, taskR);
        //将任务放在延迟的队列中
        if(!task.contains(k)){
            task.add(k);
        }
    }

    /**
     * 结束订单
     * @param taskR
     */
    public boolean endTask(Task<Runnable> taskR){
        return task.remove(taskR);
    }

}
