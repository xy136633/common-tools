package com.thirdty.thread.exception;

import java.util.concurrent.TimeUnit;

public class UserOwnUncaughtExceptionHandler implements Runnable {

	@Override
	public void run() {
		throw new RuntimeException();
	}

	public static void main(String[] args) throws InterruptedException {
		// 设置线程异常捕获器
		Thread.setDefaultUncaughtExceptionHandler(new MyUncaughtExceptionHandler("捕获器1"));
		new Thread(new UserOwnUncaughtExceptionHandler()).start();
		TimeUnit.SECONDS.sleep(1);
		new Thread(new UserOwnUncaughtExceptionHandler()).start();
		TimeUnit.SECONDS.sleep(1);
		new Thread(new UserOwnUncaughtExceptionHandler()).start();
		TimeUnit.SECONDS.sleep(1);
		new Thread(new UserOwnUncaughtExceptionHandler()).start();
	}
}
