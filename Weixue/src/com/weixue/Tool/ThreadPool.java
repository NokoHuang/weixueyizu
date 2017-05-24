package com.weixue.Tool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 线程池(单例模式)
 * 
 * @author zeda
 * 
 */
public class ThreadPool {
	// 线程池
	private static ExecutorService pool;

	public static ExecutorService getInstance() {
		if (pool == null) {
			pool = Executors.newCachedThreadPool();
		}
		return pool;
	}
}
