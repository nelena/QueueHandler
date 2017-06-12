package com.nagornaja.impl;

import com.nagornaja.api.Consumer;
import com.nagornaja.api.GeneralQueue;
import com.nagornaja.api.Item;
import com.nagornaja.api.ThreadRegistrator;
import sun.jvm.hotspot.code.SingletonBlob;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Elene on 30.03.17.
 */
public class ConsumerImpl implements Consumer<Item> {

    private static volatile ConsumerImpl instance;

    static ConsumerImpl getInstance() {
        if (instance == null) {
            synchronized (ConsumerImpl.class) {
                if (instance == null) {
                    instance = new ConsumerImpl();
                }
            }
        }
        return instance;
    }

    private GeneralQueue<Item> getQueue() {
        return GeneralQueueImpl.getInstance();
    }

    @Override
    public List<Item> getNextItems() {
        return getQueue().getNextItems();
    }

    @Override
    public List<Item> getNextItemsByGroupId(Long groupId) {
        return getQueue().getNextItemsByGroupId(groupId);
    }

    @Override
    public Long findFreeGroup() {
        return getQueue().getFreeGroupId();
    }

    @Override
    public void removeProcessedItemsByGroupId(Long groupId){
        getQueue().removeProcessedItemsByGroupId(groupId);
    }

}