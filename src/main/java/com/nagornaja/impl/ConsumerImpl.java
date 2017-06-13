package com.nagornaja.impl;

import com.nagornaja.api.Consumer;
import com.nagornaja.api.GeneralQueue;
import com.nagornaja.api.Item;
import com.nagornaja.api.ThreadRegistrator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

    private ThreadRegistrator getRegistrator(){
        return ThreadRegistratorImpl.getInstance();
    }


    @Override
    public List<Item> getNextItemByGroupId(Long groupId) {
        return getQueue().getNextItemByGroupId(groupId);
    }

    @Override
    public List<Long> findFreeGroups() {
        Set<Long> allGroupIds = getQueue().getAllGroupIds();
        Set<Long> processingGroupIds = getRegistrator().getProcessingGroupIds();
        allGroupIds.removeAll(processingGroupIds);
        return new ArrayList<>(allGroupIds);
    }


    @Override
    public boolean hasItems() {
        return !getQueue().isFinished();
    }


}