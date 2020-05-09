package com.thirdty.thread.achieve;

public class BothRunnableThreadStyle {

	public static void main(String[] args) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				System.err.println("Runnable Style");
			}
		}){
			@Override
			public void run() {
				System.err.println("Thread Style");
			}
		}.start();
	}
}
