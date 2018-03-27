package com.zbest.raft.model;

/**
 * Created by zhangbin on 2018/3/27.
 */
public class ServerState {






    private long commitIndex;//已知的最大的已经被提交的日志条目的索引值

    private long lastApplied;//最后被应用到状态机的日志条目索引值（初始化为 0，持续递增）


    private long[] nextIndexs;//对于每一个服务器，需要发送给他的下一个日志条目的索引值（初始化为领导人最后索引值加一）

    private long[] matchIndexs;//对于每一个服务器，已经复制给他的日志的最高索引值

}
