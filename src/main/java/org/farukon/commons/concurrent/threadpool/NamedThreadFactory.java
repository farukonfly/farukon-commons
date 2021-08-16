package org.farukon.commons.concurrent.threadpool;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;


public abstract class NamedThreadFactory implements ThreadFactory{
    private static final AtomicInteger poolNumber = new AtomicInteger(1);
    final ThreadGroup group;
    final AtomicInteger threadNumber = new AtomicInteger(1);
    final String threadNamePrefix;

    public abstract String threadPoolName();

    NamedThreadFactory() {
       // namePrefix = "pool-" + poolNumber.getAndIncrement() + "-thread-";
        group = new ThreadGroup(threadPoolName());       
        threadNamePrefix = threadPoolName()+"-pool-"+poolNumber.getAndIncrement()+"-thread-";
       
    }
    @Override
    public  Thread newThread(Runnable r) {
        Thread t = new Thread(group, r, threadNamePrefix
                + threadNumber.getAndIncrement(), 0);
        if (t.isDaemon()){
            t.setDaemon(false);
        }
        
        if(t.getPriority()!=Thread.MIN_PRIORITY){
            t.setPriority(Thread.MIN_PRIORITY);
        }

        return t;
    }

}
