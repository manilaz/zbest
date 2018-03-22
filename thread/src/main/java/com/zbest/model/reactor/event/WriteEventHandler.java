package com.zbest.model.reactor.event;

import com.zbest.model.reactor.deleting.Event;

/**
 * Created by zhangbin on 2018/3/22.
 */
public class WriteEventHandler extends EventHandler {

    @Override
    public void handle(Event event) {

        System.out.println(this.getClass().toString());
        // step 1: encode a message to byte[]
        // step 2: submit a write task to IOWorker thread pool
    }
}
