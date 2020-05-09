package com.thirdty.thread.achieve;

public class ThreadStyle extends Thread {

	@Override
	public void run() {
		System.err.println("Thread Style");
	}
	
	public static void main(String[] args) {
		new ThreadStyle().start();
	}

}
