package com.zbest;

import com.zbest.raft.model.reactor.deleting.Server;
import com.zbest.raft.model.reactor.deleting.Source;

import java.util.ArrayList;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );

        Server server = new Server(9999);

        server.start();


        try {
            Thread.sleep(10*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ArrayList<Source> sources = new ArrayList<>();


        Source xxxx = new Source("1", "Xxxx");
        Source xxxx2 = new Source("2", "Xxxx");
        Source xxxx3 = new Source("3", "Xxxx");
        Source xxxx4 = new Source("4", "Xxxx");
        Source xxxx5 = new Source("5", "Xxxx");
        Source xxxx6 = new Source("6", "Xxxx");

        sources.add(xxxx);
        sources.add(xxxx2);
        sources.add(xxxx3);
        sources.add(xxxx4);
        sources.add(xxxx5);
        sources.add(xxxx6);

        for (Source s : sources){
            server.addConn(s);

        }
    }
}
