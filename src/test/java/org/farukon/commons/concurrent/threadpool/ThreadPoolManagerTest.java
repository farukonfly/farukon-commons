package org.farukon.commons.concurrent.threadpool;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ThreadPoolManagerTest {
    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolManagerTest.class);
    private static final ThreadPoolManager THREAD_POOL_MANAGER = new ThreadPoolManager();
    
    @BeforeAll
    static void startingThreadPoolManager() {
    	THREAD_POOL_MANAGER.startThreadPool("farukon-jpa");
    }
    
    @Test
    void testStartThreadPool() {
		for (int i = 0; i < 1000; i++) {
			R r = new R();
			r.num = i;
			THREAD_POOL_MANAGER.execute(r);
		}
    }
    static class R implements Runnable{
		private int num;
		@Override
		public void run() {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			    logger.error(e.getMessage(),e);
			}
			logger.debug(this.num + ":" + Thread.currentThread().getName() + " is running." + THREAD_POOL_MANAGER.getThreadPool().getQueue().remainingCapacity());
			
		}
		
	}
}
