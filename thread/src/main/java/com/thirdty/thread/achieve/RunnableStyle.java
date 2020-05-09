package com.thirdty.thread.achieve;

public class RunnableStyle implements Runnable {

	@Override
	public void run() {
		System.err.println("Runnable Style");

	}

	public static void main(String[] args) {
		new Thread(new RunnableStyle()).start();
	}
}
