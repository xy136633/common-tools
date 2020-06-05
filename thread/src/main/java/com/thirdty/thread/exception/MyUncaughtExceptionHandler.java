package com.thirdty.thread.exception;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MyUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

	private String name;
	
	public MyUncaughtExceptionHandler(String name) {
		this.name = name;
	}

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		Logger logger = Logger.getAnonymousLogger();
		logger.log(Level.WARNING, name + "捕获了线程异常中止---" + t.getName(), e);
	}

}
