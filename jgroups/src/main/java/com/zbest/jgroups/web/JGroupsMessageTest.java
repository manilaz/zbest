package com.zbest.jgroups.web;

import com.zbest.jgroups.listener.InitContextListener;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by zhangbin on 2018/6/24.
 */
@RestController
public class JGroupsMessageTest {


    @RequestMapping("/send")
    public String send(String message){

        Message msg = new Message(null, message);

        try {
            JChannel channel = InitContextListener.jChannel.send(msg);

            return channel.getView().getMembers().toString();
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

}
