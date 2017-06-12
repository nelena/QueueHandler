package com.nagornaja.impl;

import com.nagornaja.api.GeneralQueue;
import com.nagornaja.api.Item;
import com.nagornaja.api.ThreadRegistrator;
import com.nagornaja.old.QueueServiceImpl;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * Created by Elene on 04.06.17.
 */
public class GeneralQueueImpl implements GeneralQueue<Item> {
    private static volatile GeneralQueueImpl instance;

    private AtomicBoolean isAddingFinished = new AtomicBoolean(false);

    private ConcurrentMap<Long, SubQueueImpl> mapOfSubqueues = new ConcurrentHashMap<>();


    static GeneralQueueImpl getInstance() {
        if (instance == null) {
            synchronized (QueueServiceImpl.class) {
                if (instance == null) {
                    instance = new GeneralQueueImpl();
                }
            }
        }
        return instance;
    }

    private ThreadRegistrator getRegistrator() {
        return ThreadRegistratorImpl.getInstance();
    }

    private Set<Long> getFreeGroupIds() {
        return mapOfSubqueues.keySet().stream()
                .filter(k -> !mapOfSubqueues.get(k).isProcessed())
                .collect(Collectors.toSet());
    }

    @Override
    public Long getFreeGroupId() {
        Set<Long> freeGroupIds = getFreeGroupIds();
        final int[] max = {0};
        final Long[] res = {0L};
        mapOfSubqueues.forEach((groupId, subQueue) -> {
            if (subQueue.size() > max[0] && freeGroupIds.contains(groupId)) {
                max[0] = subQueue.size();
                res[0] = groupId;
            }
        });
        return res[0];
    }

    @Override
    public Set<Long> getAllGroupIds() {
        return new HashSet<>(mapOfSubqueues.keySet());
    }

    private boolean isGroupExists(Long groupId) {
        return mapOfSubqueues.containsKey(groupId);
    }

    @Override
    public List<Item> getNextItems() {

        Long groupId = getFreeGroupId();

        return getNextItemsByGroupId(groupId);
    }

    @Override
    public List<Item> getNextItemsByGroupId(Long groupId) {
        List<Item> res = new ArrayList<>();

        if (isGroupExists(groupId)) {
            res.addAll(mapOfSubqueues.get(groupId).getNextItemsForProcessing());
        }
        return res;
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
            res = res || subQueue.hasFreeItems();
        }
        return res;
    }

    @Override
    public void removeProcessedItemsByGroupId(Long groupId) {
        mapOfSubqueues.get(groupId).removeProcessedItems();
    }



    @Override
    public SubQueueImpl createNotExistingSubqueue(Long groupId) {
        mapOfSubqueues.put(groupId, new SubQueueImpl());
        System.out.println(LocalTime.now() + ": CREATE GROUP: [ GROUP: " + groupId + " ]");
        return mapOfSubqueues.get(groupId);
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
    public void setIsAddingFinished(boolean isAddingFinished) {
        this.isAddingFinished.set(isAddingFinished);
    }
}
