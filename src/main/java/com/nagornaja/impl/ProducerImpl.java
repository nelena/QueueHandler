package com.nagornaja.impl;

import com.nagornaja.Utils;
import com.nagornaja.api.GeneralQueue;
import com.nagornaja.api.Item;
import com.nagornaja.api.Producer;

import java.time.LocalTime;
import java.util.Random;

/**
 * Created by Elene on 31.03.17.
 */
public class ProducerImpl implements Producer<Item> {
    private Long groupsCount;
    private Long itemsCount;

    public ProducerImpl(long groupsCount, long itemsCount){
        this.groupsCount = groupsCount;
        this.itemsCount = itemsCount;
    }


    public ProducerImpl(){
        groupsCount = (long) Utils.generateRandom(1, 3);
        itemsCount = (long) Utils.generateRandom(groupsCount.intValue() + 1, 4);
    }


    @Override
    public void run() {
        Random random = new Random();
        for(int i = 0; i < itemsCount; i++) {
            Item item = new ItemImpl(Long.valueOf(random.nextInt(itemsCount.intValue())), Long.valueOf(random.nextInt(groupsCount.intValue())));
            System.out.println(LocalTime.now() + ": CREATE ITEM: " + item);
            putItem(item);
            try {
                Thread.sleep(1L);
            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        getQueue().setIsAddingFinished();
        System.out.println(LocalTime.now() + ": ADDING FINISHED.");
    }

    @Override
    public void putItem(Item item) {
        Long groupId = item.getGroupId();
        if(groupId == null){
            throw new IllegalArgumentException("Item has failed parameter: groupId == null");
        }
        getQueue().putItem(item);
    }

    @Override
    public int getGroupsCount() {
        return groupsCount.intValue();
    }

    private GeneralQueue<Item> getQueue(){
        return GeneralQueueImpl.getInstance();
    }
}
