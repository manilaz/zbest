package com.zbest.jgroups.listener;

/**
 * Created by zhangbin on 2018/7/2.
 */
public class SecurityRestrictionsJob implements Runnable {

    private final String guid;

    private int count;

    @Override
    public void run() {
        System.out.println("处理guid====="+guid+",count:"+count);
    }

    public SecurityRestrictionsJob(String guid) {
        this.guid = guid;
        this.count = 0;
    }

    public String getGuid() {
        return guid;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public int hashCode() {
        return guid.hashCode();
    }

    @Override
    public boolean equals(Object obj) {

        if(obj instanceof SecurityRestrictionsJob){
            SecurityRestrictionsJob job = (SecurityRestrictionsJob) obj;
            if(this.guid.equals(job.getGuid())){
                System.out.println("***************");
                job.setCount(job.getCount() + 1);
            }
            return this.guid.equals(job.getGuid());
        }

        return super.equals(obj);
    }
}
