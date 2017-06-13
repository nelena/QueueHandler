package com.nagornaja.impl;

import com.nagornaja.api.GeneralQueue;
import com.nagornaja.api.Item;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Elene on 04.06.17.
 */
public class GeneralQueueImpl implements GeneralQueue<Item> {
    private static volatile GeneralQueueImpl instance;

    private AtomicBoolean isAddingFinished = new AtomicBoolean(false);

    private ConcurrentMap<Long, SubQueueImpl> mapOfSubqueues = new ConcurrentHashMap<>();


    static GeneralQueueImpl getInstance() {
        if (instance == null) {
            synchronized (GeneralQueueImpl.class) {
                if (instance == null) {
                    instance = new GeneralQueueImpl();
                }
            }
        }
        return instance;
    }


    @Override
    public Set<Long> getAllGroupIds() {
        mapOfSubqueues.values().forEach((q) -> {
            if(q.isEmpty()){
                removeProcessedGroup(q.getGroupId());
            }
        });
        return new HashSet<>(mapOfSubqueues.keySet());
    }


    @Override
    public List<Item> getNextItemByGroupId(Long groupId) {
        List<Item> res = new ArrayList<>();

        if (isGroupExists(groupId)) {
            res.add(mapOfSubqueues.get(groupId).getNextItemForProcessing());
        }
        return res;
    }

    private boolean isGroupExists(Long groupId) {
        return groupId != null && mapOfSubqueues.containsKey(groupId);
    }

    @Override
    public void putItem(Item item) {
        Long groupId = item.getGroupId();
        if (!isGroupExists(groupId)) {
            createNotExistingSubqueue(groupId);
        }
        addItemToGroup(item);
    }

    private void addItemToGroup(Item item) {
        Long groupId = item.getGroupId();
        if (!isGroupExists(groupId)) {
            createNotExistingSubqueue(groupId);
        }
        mapOfSubqueues.get(groupId).add(item);
        System.out.println(LocalTime.now() + ": ITEM [ " + item.getItemId() + " ] ADDED TO [ GROUP: " + groupId + " ]"
                + printSubqueueByGroupId(mapOfSubqueues.get(groupId)));
    }

    @Override
    public boolean hasItems() {
        boolean res = false;
        for (SubQueueImpl subQueue : mapOfSubqueues.values()) {
            res = res || !subQueue.isEmpty();
        }
        return res;
    }

    @Override
    public void removeProcessedGroup(Long groupId) {
        mapOfSubqueues.remove(groupId);
    }


    @Override
    public void createNotExistingSubqueue(Long groupId) {
        mapOfSubqueues.put(groupId, new SubQueueImpl());
        System.out.println(LocalTime.now() + ": CREATE GROUP: [ GROUP: " + groupId + " ]");
        mapOfSubqueues.get(groupId);
    }


    private String printSubqueueByGroupId (BlockingQueue < Item > queue) {
        StringBuilder res = new StringBuilder();
        for (Item item : queue) {
            res.append("[ ").append(item.getItemId()).append(" ] ");
        }
        return res.toString();
    }

    @Override
    public boolean getIsAddingFinished() {
        return isAddingFinished.get();
    }

    @Override
    public boolean isFinished() {
        return getIsAddingFinished() && !hasItems();
    }

    @Override
    public void setIsAddingFinished() {
        this.isAddingFinished.set(true);
    }
}
