package com.nagornaja.impl;

import com.nagornaja.api.Item;
import com.nagornaja.api.SubQueue;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Elene on 04.06.17.
 */
public class SubQueueImpl extends PriorityBlockingQueue<Item>{

    private static int AMOUNT_RESTRICTION = 10;
    private Lock lock = new ReentrantLock();
    private Long groupId;
    private List<Item> processingItems = new ArrayList<>();
    private AtomicBoolean isProcessed = new AtomicBoolean(false);

    @Override
    public boolean add(Item item) {
        if(groupId == null){
            groupId = item.getGroupId();
        }
        return super.add(item);
    }

    public void removeProcessedItems(){
        removeAll(processingItems);
        processingItems.clear();
        setProcessed(false);
    }

    public List<Item> getNextItemsForProcessing(){
        if(this.size() < AMOUNT_RESTRICTION){
            processingItems.addAll(this);
        }else {
            for (int i = 0; i < AMOUNT_RESTRICTION; i++){
                processingItems.add(this.peek());
            }
        }
        setProcessed(true);
        return processingItems;
    }


    public void lock() {
        System.out.println(LocalTime.now() + ": [ " + Thread.currentThread().getName() + " ] LOCKED [ GROUP: " + getGroupId() + " ]");
        lock.lock();
    }


    public void unlock() {
        System.out.println(LocalTime.now() + ": [ " + Thread.currentThread().getName() + " ] UNLOCK [ GROUP: " + getGroupId() + " ]");
        lock.unlock();
    }


    public Long getGroupId() {
        if(groupId != null){
            return groupId;
        }else throw new IllegalArgumentException();
    }

    public boolean isProcessed() {
        return isProcessed.get();
    }

    private void setProcessed(boolean isProcessed) {
        this.isProcessed.set(isProcessed);
    }

    public boolean hasFreeItems(){
        return !isProcessed() && isEmpty();
    }
}
