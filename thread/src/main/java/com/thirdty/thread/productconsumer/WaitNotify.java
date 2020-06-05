package com.thirdty.thread.productconsumer;

import java.util.Date;
import java.util.LinkedList;

public class WaitNotify {
	
	public static void main(String[] args) {
		EventStorage storage = new EventStorage();
		Product product = new Product(storage);
		Consumer consumer = new Consumer(storage);
		new Thread(product).start();
		new Thread(consumer).start();
	}
}

class Product implements Runnable{
	private EventStorage storage;

	public Product(EventStorage storage) {
		this.storage = storage;
	}

	@Override
	public void run() {
		for (int i=0; i<100; i++){
			storage.put();
		}
	}
}

class Consumer implements Runnable {

	private EventStorage storage;

	public Consumer(EventStorage storage) {
		this.storage = storage;
	}
	
	@Override
	public void run() {
		for (int i=0; i<100; i++){
			storage.take();
		}
	}
	
}

class EventStorage {
	private int maxSize;
	private LinkedList<Date> storage;
	public EventStorage() {
		this.maxSize = 10;
		this.storage = new LinkedList<>();
	}
	
	public synchronized void put(){
		while(storage.size() == maxSize){
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		storage.add(new Date());
		System.err.println("产品数量---=--"+storage.size());
		notify();
	}
	
	public synchronized void take(){
		while(storage.isEmpty()){
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.err.println("消费----"+storage.poll());
		notify();
	}
}