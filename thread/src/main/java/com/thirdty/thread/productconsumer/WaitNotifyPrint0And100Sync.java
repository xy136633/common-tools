package com.thirdty.thread.productconsumer;

public class WaitNotifyPrint0And100Sync {

	protected static int count = 0;
	protected static final Object lock = new Object();

	public static void main(String[] args) {
		new Thread(new Runnable() {
			public void run() {
				while (count < 100){
					synchronized (lock){
//						if (count % 2 == 0)
						if ((count & 1) == 0){
							System.err.println(Thread.currentThread().getName() + ": " + count++);
						}
					}
				}
			}
		}, "偶数").start();
		
		new Thread(new Runnable() {
			public void run() {
				while (count < 100){
					synchronized (lock){
//						if (count % 2 == 1)
						if ((count & 1) == 1){
							System.err.println(Thread.currentThread().getName() + ": " + count++);
						}
					}
				}
			}
		}, "奇数").start();
	}
}
