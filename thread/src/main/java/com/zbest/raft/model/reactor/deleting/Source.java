package com.zbest.raft.model.reactor.deleting;

import java.util.Date;

/**
 * Created by zhangbin on 2018/3/22.
 */
public class Source {

    private final String traceId;

    private final String req;

    private Date begin;

    private Date end;

    public Source(String traceId, String req) {
        this.traceId = traceId;
        this.req = req;
    }

    public Date getBegin() {
        return begin;
    }

    public void setBegin(Date begin) {
        this.begin = begin;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }
}
