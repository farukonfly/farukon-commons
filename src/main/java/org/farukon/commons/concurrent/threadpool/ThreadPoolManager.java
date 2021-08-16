package org.farukon.commons.concurrent.threadpool;

import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Step1: {@code ThreadPoolManager threadPoolManager =  new ThreadPoolManager()}
 * Step2: {@code threadPoolManager.startThreadPool(arguments)}
 * @author farukon
 *
 */
public class ThreadPoolManager {

	
	private static final Logger logger = LoggerFactory.getLogger(ThreadPoolManager.class);
	private static final int DEFAULT_CORE_POOL_SIZE = 10;
	private static final int DEFAULT_BUFFER_WORK_QUEUE_SIZE = 50;
	private static final int DEFAULT_MAXIMUM_POOL_SIZE = 80;
	private static final long DEFAULT_KEEP_ALIVE_TIME = 30;
	private static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.SECONDS;
	private volatile boolean isStarted = false;
	private ThreadPool pool;

	public void startThreadPool(String threadPoolName) {
		this.doStartThreadPool(threadPoolName, DEFAULT_CORE_POOL_SIZE, DEFAULT_BUFFER_WORK_QUEUE_SIZE,
				DEFAULT_MAXIMUM_POOL_SIZE, DEFAULT_KEEP_ALIVE_TIME, DEFAULT_TIME_UNIT,
				new ThreadPoolExecutor.CallerRunsPolicy());
	}

	public void startThreadPool(String threadPoolName, int corePoolSize, int workQueueSize,
			int maximumPoolSize, long keepAliveTime, TimeUnit unit) {
		this.doStartThreadPool(threadPoolName, corePoolSize, workQueueSize, maximumPoolSize, keepAliveTime, unit,
				new ThreadPoolExecutor.CallerRunsPolicy());
	}

	private void doStartThreadPool(String threadPoolName, int corePoolSize, int workQueueSize,
			int maximumPoolSize, long keepAliveTime, TimeUnit unit, RejectedExecutionHandler handler) {
			if (this.isStarted == false) {
				logger.info("Thread pool " + "[" + threadPoolName + "]" + " is starting up");
				this.pool = new ThreadPool(Objects.equals(corePoolSize, 0) ? DEFAULT_CORE_POOL_SIZE : corePoolSize,
						Objects.equals(maximumPoolSize, 0) ? DEFAULT_MAXIMUM_POOL_SIZE : maximumPoolSize,
						Objects.equals(keepAliveTime, 0L) ? DEFAULT_KEEP_ALIVE_TIME : keepAliveTime,
						Objects.isNull(unit) ? DEFAULT_TIME_UNIT : unit,
						Objects.equals(workQueueSize, 0) ? new LinkedBlockingQueue<>(DEFAULT_BUFFER_WORK_QUEUE_SIZE)
								: new LinkedBlockingQueue<>(workQueueSize),
						new NamedThreadFactory() {
							@Override
							public String threadPoolName() {
								return threadPoolName;
							}
						}, handler);

				logger.info("Thread pool " + "[" + threadPoolName + "]" + " started" + " {"+System.lineSeparator()
						+ "Core Pool Size="+ this.pool.getCorePoolSize() + System.lineSeparator() 
						+ "Work Queue Size="+ this.pool.getQueue().remainingCapacity() + System.lineSeparator() 
						+ "Maximum Pool Size="+ this.pool.getMaximumPoolSize() + System.lineSeparator()
						+ "Keep Alive Time="+ this.pool.getKeepAliveTime(Objects.isNull(unit) ? DEFAULT_TIME_UNIT : unit) + " " + (Objects.isNull(unit) ? DEFAULT_TIME_UNIT : unit).toString().toLowerCase()+ System.lineSeparator() 
						+ "}");

				this.isStarted = true;
				logger.info("Thread pool " + "[" + threadPoolName + "]" + " started successfully");

			}
		
	}

	protected ThreadPool getThreadPool() {
		return this.pool;
	}

	public void shutDownThreadPool() {
		if (this.pool == null) {
			return;
		}
		this.pool.shutdown();
		while (!this.pool.isShutdown()) {
		}
		logger.info("Thread Pool shutdowned");
		this.isStarted = false;

	}

	public void pauseThreadPool() {
		if (this.pool != null) {
			this.pool.pause();
		}
	}

	public void resumeThreadPool() {
		if (this.pool != null) {
			this.pool.resume();
		}
	}

	public boolean isStarted() {
		return this.isStarted;
	}

	public void execute(Runnable runnable) {
		this.pool.execute(runnable);
	}

}
