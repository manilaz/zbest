package com.zbest.model.reactor.deleting;

import com.zbest.model.reactor.event.AcceptEventHandler;

/**
 * Created by zhangbin on 2018/3/22.
 */
public class Server {

    Selector selector = new Selector();

    Dispatcher eventLoop = new Dispatcher(selector);

    Acceptor acceptor;

    public Server(int port){
        acceptor = new Acceptor(selector,port);
    }

    public void start(){
        eventLoop.registEventHandler(EventType.ACCEPT,new AcceptEventHandler(selector));
        new Thread(acceptor,"Acceptor-"+acceptor.getPort()).start();
        eventLoop.handleEvents();
    }

    public void addConn(Source source){

        acceptor.aNewConnection(source);
    }


}
