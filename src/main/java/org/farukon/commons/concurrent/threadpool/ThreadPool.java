package org.farukon.commons.concurrent.threadpool;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

class ThreadPool extends ThreadPoolExecutor{
	private ThreadPoolStatus status_ = ThreadPoolStatus.shutdowned;
	
	private boolean isPaused;
    private ReentrantLock pauseLock = new ReentrantLock();
    private Condition unpaused = pauseLock.newCondition();
	
	public ThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime
			, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory,
			RejectedExecutionHandler handler) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
		status_ = ThreadPoolStatus.running;
	}
	
	@Override
	protected void beforeExecute(Thread t, Runnable r) {
	    super.beforeExecute(t, r);
	    pauseLock.lock();
	    try {
	    	while (isPaused) unpaused.await();
	    } catch(InterruptedException ie) {
	    	t.interrupt();
	    } finally {
	    	pauseLock.unlock();
	    }
	}
	 
    void pause() {
    	pauseLock.lock();
    	try {
    		isPaused = true;
    	} finally {
    		pauseLock.unlock();
    		status_ = ThreadPoolStatus.paused;
    	}
   }
	 
    void resume() {
    	pauseLock.lock();
	    try {
	    	isPaused = false;
	    	unpaused.signalAll();
	    } finally {
	    	pauseLock.unlock();
	    	status_ = ThreadPoolStatus.running;
	    }
	}

    public ThreadPoolStatus status() {
    	return status_;
    }
	
	@Override
	public void shutdown() {
		super.shutdown();
		status_ = ThreadPoolStatus.shutdowned;
	}

	@Override
	public List<Runnable> shutdownNow() {
		status_ = ThreadPoolStatus.shutdowned;
		return super.shutdownNow();
		
	}
	
	enum ThreadPoolStatus {
		running, shutdowned, paused;
	}
}
