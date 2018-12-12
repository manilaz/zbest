package com.zbest.jgroups.web;

import com.zbest.jgroups.listener.SecurityRestrictionsJob;
import com.zbest.jgroups.listener.TaskQueueDaemonThread;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by zhangbin on 2018/7/2.
 */
@RestController
public class DelayedTest {

    private static TaskQueueDaemonThread thread = TaskQueueDaemonThread.getInstance();

    @RequestMapping("/put")
    public String put(String guid){


        SecurityRestrictionsJob job = new SecurityRestrictionsJob(guid);

        thread.put(10000,job);

        return guid;
    }


}
