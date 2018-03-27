package com.zbest.raft.model.factory;

/**
 * Created by zhangbin on 2018/3/27.
 */
public class ContextFactory implements AbstractContextFactory {

    public AbstractStateMachine createStateMachine() {
        return null;
    }

    public AbstractClusterConfiguration createClusterConfiguration() {
        return null;
    }

    public AbstractServerConfiguration createServerConfiguration() {
        return null;
    }
}
