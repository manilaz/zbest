package com.zbest.websocket.web;

import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhangbin on 2018/6/12.
 */
@Component("socketHandler")
@ServerEndpoint(value = "/websocket/{sId}")
public class SocketHandler {

    private static int onlineCount = 0;

    private static Map<String,Session> map = new HashMap<>();

    private Session session;

    private String sId;


    @OnOpen
    public void onOpen(@PathParam("sId") String sId, Session session) throws IOException{

        this.session = session;
        this.sId = sId;

        map.put(sId,session);

        session.getBasicRemote().sendText("连接成功");

        System.out.println("有新链接加入!当前在线人数为" + map.size());
    }


    @OnClose
    public void onClose(){

        map.remove(this.sId);

        System.out.println("有连接退出，当前在线人数为" + map.size());

    }

    @OnError
    public void onError(Session session, Throwable error){


        System.out.println("用户"+this.sId+"连接失败");
    }

    public Boolean sendMessageToUser(String sId,String message){

        if(map.containsKey(sId)){
            Session session = map.get(sId);
            try {
                session.getBasicRemote().sendText(message);
                return true;
            } catch (IOException e) {

                System.out.println("发送失败了..");
                e.printStackTrace();
                return false;
            }
        }

        System.out.println("发送失败了");
        return false;

    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public String getsId() {
        return sId;
    }

    public void setsId(String sId) {
        this.sId = sId;
    }
}
