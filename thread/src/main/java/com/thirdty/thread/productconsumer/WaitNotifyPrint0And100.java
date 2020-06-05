package com.thirdty.thread.productconsumer;

public class WaitNotifyPrint0And100 {

	protected static int count = 0;
	protected static final Object lock = new Object();
	
	public static void main(String[] args) {
		new Thread(new TurningRunnable()).start();
		new Thread(new TurningRunnable()).start();
	}
	
	static class TurningRunnable implements Runnable{
		
		@Override
		public void run() {
			// 拿到锁就打印，打印完唤醒另外一个线程，然后自己休眠
			while (count <= 100){
				synchronized (lock) {
					System.err.println(Thread.currentThread().getName() + ": " + count++);
					lock.notify();
					if (count <= 100){
						try {
							lock.wait();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}
    }

}