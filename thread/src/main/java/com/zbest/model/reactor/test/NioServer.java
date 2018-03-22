package com.zbest.model.reactor.test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Reactor 主从Reactor模式实现
 *
 * Acceptor,其实个人认为，这里就是服务端角色
 * @author Administrator
 *
 */
public class NioServer {

    private static final int DEFAULT_PORT = 9080;

    public static void main(String[] args) {

        new Thread(new Acceptor()).start();

    }


    private static class Acceptor implements Runnable {

        // main Reactor 线程池，用于处理客户端的连接请求
        private static ExecutorService mainReactor = Executors.newSingleThreadExecutor();

        public void run() {
            // TODO Auto-generated method stub
            ServerSocketChannel ssc = null;

            try {
                ssc = ServerSocketChannel.open();
                ssc.configureBlocking(false);
                ssc.bind(new InetSocketAddress(DEFAULT_PORT));

                //转发到 MainReactor反应堆
                dispatch(ssc);

                System.out.println("服务端成功启动。。。。。。");

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        private void dispatch(ServerSocketChannel ssc) {
            mainReactor.submit(new MainReactor(ssc));
        }

    }


}