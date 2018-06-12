package com.zbest.websocket.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by zhangbin on 2018/6/12.
 */

@Controller
@RequestMapping("/test")
public class SocketTest {

    @Autowired
    private SocketHandler socketHandler;

    @RequestMapping(value="/TestWS",method= RequestMethod.GET)
    @ResponseBody
    public String TestWS(@RequestParam(value="sId",required=true) String sId,
                         @RequestParam(value="message",required=true) String message){
        System.out.println("收到发送请求，向用户"+sId+"的消息："+message);
        if(socketHandler.sendMessageToUser(sId, message)){
            return "发送成功";
        }else{
            return "发送失败";
        }
    }

}
