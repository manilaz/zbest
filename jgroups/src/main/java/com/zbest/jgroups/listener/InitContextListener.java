package com.zbest.jgroups.listener;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.stack.IpAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.JsonSimpleJsonParser;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Created by zhangbin on 2018/6/21.
 */
@WebListener
public class InitContextListener implements ServletContextListener{

    @Value("${jgroups.config.url}")
    private String url;

    Logger logger = LoggerFactory.getLogger(InitContextListener.class);

    public static JChannel jChannel;

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

        logger.info("====================项目结束=======================");
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {

        logger.info("====================项目初始化=====================");

        try {

            jChannel = new JChannel(url);
            jChannel.setReceiver(new Node(jChannel));
            jChannel.connect("cs");

            logger.info("=============={}==================",jChannel.getViewAsString());

        } catch (Exception e) {

            new JsonSimpleJsonParser();
            logger.error("创建JChannel失败", e.getMessage());
        }

    }
}
