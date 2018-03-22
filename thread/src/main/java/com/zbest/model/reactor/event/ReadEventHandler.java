package com.zbest.model.reactor.event;

import com.zbest.model.reactor.deleting.Event;

/**
 * Created by zhangbin on 2018/3/22.
 */
public class ReadEventHandler extends EventHandler {

    // private Pipeline pipeline;

    @Override
    public void handle(Event event) {

        System.out.println(this.getClass().toString());
        // create channel with a pipeline
        // register the channel to this event dispatcher or a child event dispatcher


        // handle event use the pipeline :
        // step 1:  read to a frame buffer
        // step 2:  use frame decoder to decode buffer as a message (maybe a business object)
        // step 3:  handle the message or submit the message to business thread pool
        // step 4:  register a message event

    }
}
