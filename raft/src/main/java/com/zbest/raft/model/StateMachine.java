package com.zbest.raft.model;

import com.zbest.raft.model.factory.AbstractStateMachine;

/**
 * Created by zhangbin on 2018/3/27.
 */
public class StateMachine implements AbstractStateMachine{

    private String Id;//当前状态机id

    private long currentTerm;//服务器最后一次知道的任期号（初始化为 0，持续递增）

    private String votedFor;//在当前获得选票的候选人的 Id

    private ServerType type;//当前节点状态

    private Log[] logs;//日志条目集；每一个条目包含一个用户状态机执行的指令，和收到时的任期号

    public long getCurrentTerm() {
        return currentTerm;
    }

    public void setCurrentTerm(long currentTerm) {
        this.currentTerm = currentTerm;
    }

    public String getVotedFor() {
        return votedFor;
    }

    public void setVotedFor(String votedFor) {
        this.votedFor = votedFor;
    }

    public ServerType getType() {
        return type;
    }

    public void setType(ServerType type) {
        this.type = type;
    }

    public Log[] getLogs() {
        return logs;
    }

    public void setLogs(Log[] logs) {
        this.logs = logs;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }
}
