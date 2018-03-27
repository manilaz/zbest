package com.zbest.raft.model;

/**
 * Created by zhangbin on 2018/3/27.
 */
public class Log {

    private long term;//领导人的任期号
    private String[] entries;//准备存储的日志条目（表示心跳时为空；一次性发送多个是为了提高效率）
    private String leaderId;//领导人的 Id，以便于跟随者重定向请求
    private long prevLogIndex;//新的日志条目紧随之前的索引值
    private long prevLogTerm;//prevLogIndex 条目的任期号
    private long leaderCommit;//领导人已经提交的日志的索引值

    public long getTerm() {
        return term;
    }

    public void setTerm(long term) {
        this.term = term;
    }

    public String[] getEntries() {
        return entries;
    }

    public void setEntries(String[] entries) {
        this.entries = entries;
    }

    public String getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(String leaderId) {
        this.leaderId = leaderId;
    }

    public long getPrevLogIndex() {
        return prevLogIndex;
    }

    public void setPrevLogIndex(long prevLogIndex) {
        this.prevLogIndex = prevLogIndex;
    }

    public long getPrevLogTerm() {
        return prevLogTerm;
    }

    public void setPrevLogTerm(long prevLogTerm) {
        this.prevLogTerm = prevLogTerm;
    }

    public long getLeaderCommit() {
        return leaderCommit;
    }

    public void setLeaderCommit(long leaderCommit) {
        this.leaderCommit = leaderCommit;
    }
}
