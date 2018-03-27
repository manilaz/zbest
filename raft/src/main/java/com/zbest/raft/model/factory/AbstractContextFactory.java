package com.zbest.raft.model.factory;


/**
 * Created by zhangbin on 2018/3/27.
 */
public interface AbstractContextFactory {

    AbstractStateMachine createStateMachine();

    AbstractClusterConfiguration createClusterConfiguration();

    AbstractServerConfiguration createServerConfiguration();


}
