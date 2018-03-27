package com.zbest.raft.util;

/**
 * Created by zhangbin on 2018/3/27.
 */
public enum PropertiesEnum {

    Server("server"),Cluster("cluster");

    private final String SUFFIX = ".properties";

    PropertiesEnum(String name) {
        this.name = name;
    }
    private String name;

    public String getName(){
        return this.name;
    }

    public String getPath(){
        return this.getClass().getResource("/").getPath()+this.getName()+this.SUFFIX;
    }
}
