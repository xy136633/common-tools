package com.thirdty.tools.poi.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 阻塞队列，用于分段提供数据，避免一次产生太多数据导致内存溢出
 * 
 * @param <T> 队列元素类型
 * @author whk00104/金豆-小蝴蝶
 * @since 2018-04
 */
public class BlockQueueSupplier<T> implements Supplier<T> {

    /**
     * 获取一个元素，当队列生产速度低于消费速度时，会阻塞等待，当队列结束时会直接返回null
     * 
     * @author whk00104/金豆-小蝴蝶
     */
    @Override
    public T get() {
        T data = queue.poll();
        if (data != null) {
            return data;
        }
        if (isOver) {
            return null;
        }
        try {
            while (data == null && !isOver) {
                data = queue.poll(1, TimeUnit.SECONDS);
            }
            return data;
        } catch (InterruptedException e) {
            return null;
        }
    }

    /**
     * 放置一个元素，，当队列生产速度高于消费速度并且队列已满时，会阻塞，当队列数据生成标志为已结束后，不再放入数据
     * 
     * @author whk00104/金豆-小蝴蝶
     * @param data
     */
    public void put(T data) {
        if (isOver) {
            throw new RuntimeException("队列生产已结束");
        }
        try {
            queue.put(data);
        } catch (InterruptedException e) {}
    }

    /**
     * 设置队列生产者状态为已结束
     * 
     * @author whk00104/金豆-小蝴蝶
     */
    public void over() {
        this.isOver = true;
    }
    
    /**
     * 
    * @Title: isQueueOver  
    * @Description: 获取当前队列状态
    * @param @return    参数  
    * @return boolean    返回类型  
    * @throws
     */
    public boolean isQueueOver() {
    	return this.isOver;
    }

    // 队列大小
    private static final int QUEUE_SIZE = 10000;
    // 队列数据生成已结束
    private boolean isOver = false;
    // 放置元素的队列
    private BlockingQueue<T> queue = new LinkedBlockingQueue<>(QUEUE_SIZE);
}
