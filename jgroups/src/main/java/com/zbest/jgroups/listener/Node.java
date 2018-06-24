package com.zbest.jgroups.listener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.jgroups.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhangbin on 2018/6/24.
 */
public class Node extends ReceiverAdapter {

    Logger logger = LoggerFactory.getLogger(Node.class);


    /**
     * 以此作为节点间初始化的同步数据.
     */
    private Map<String, String> cacheData = new HashMap<String, String>();

    private ReentrantLock lock = new ReentrantLock();

    private JChannel jChannel;

    public JChannel getjChannel() {
        return jChannel;
    }

    public void setjChannel(JChannel jChannel) {
        this.jChannel = jChannel;
    }

    public Node(JChannel jChannel){
        this.jChannel = jChannel;
    }
    /**
     *
     * <pre>
     * 发送消息给目标地址.
     * </pre>
     *
     * @param dest
     *            为空表示发给所有节点.
     * @param textMsg
     *            消息.
     */
    public void sendMsg(Address dest, Object textMsg) {
        Message msg = new Message(dest, textMsg);
        try {
            jChannel.send(msg);
        } catch (Exception e) {
            logger.error("消息发送失败!", e);
            // 应自定异常,最好是自定义Exception类型!
            throw new RuntimeException("消息发送失败!", e);
        }
    }

    @Override
    public void receive(Message msg) {
        //当前节点不接收自己发送到通道当中的消息.
//        if (msg.getSrc().equals(jChannel.getAddress())) {
//            return;
//        }
        logger.info(msg.getObject());
    }

    @Override
    public void viewAccepted(View view) {
        logger.info("当前成员[" + this.jChannel.getAddressAsString() + "]");
        logger.info(view.getCreator().toString());
        logger.info(view.getMembers().toString());
        logger.info("当前节点数据:" + cacheData);
    }

}
