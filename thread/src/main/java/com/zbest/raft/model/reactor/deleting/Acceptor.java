package com.zbest.raft.model.reactor.deleting;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by zhangbin on 2018/3/22.
 */
public class Acceptor implements Runnable {


    private int port; // server socket port

    private Selector selector;

    // 代表 serversocket
    private final BlockingQueue<Source> sourceQueue = new LinkedBlockingQueue<Source>();

    public Acceptor(Selector selector, int port) {
        this.selector = selector;
        this.port = port;
    }

    public void aNewConnection(Source source) {
        sourceQueue.offer(source);
    }

    public int getPort() {
        return this.port;
    }


    @Override
    public void run() {
        while (true) {

            Source source = null;
            try {
                // 相当于 serversocket.accept()
                source = sourceQueue.take();
            } catch (InterruptedException e) {
                // ignore it;
            }

            if (source != null) {
                Event acceptEvent = new Event(EventType.ACCEPT,source);
                selector.addEvent(acceptEvent);
            }

        }
    }
}
