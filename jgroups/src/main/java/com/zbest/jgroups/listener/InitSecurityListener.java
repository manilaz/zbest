package com.zbest.jgroups.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Created by zhangbin on 2018/7/2.
 */
@WebListener
public class InitSecurityListener implements ServletContextListener {

    private static TaskQueueDaemonThread thread = TaskQueueDaemonThread.getInstance();

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {

        System.out.println("InitSecurityListener =============init");
        thread.init();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        System.out.println("InitSecurityListener =============destroy");
    }
}
