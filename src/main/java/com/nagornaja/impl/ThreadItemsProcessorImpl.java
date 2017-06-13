package com.nagornaja.impl;

import com.nagornaja.Utils;
import com.nagornaja.api.*;

import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Elene on 12.06.17.
 */
public class ThreadItemsProcessorImpl extends Thread implements ThreadItemsProcessor<Item> {

    private static final int THREASHOLD_FOR_ONE_GROUP_PROCESSING = 3;

    private Long currentGroupId;
    private int threasholdForProcessing;
    private int processedItemsForGroup;
    private List<Item> processingItems = new LinkedList<>();

    public ThreadItemsProcessorImpl() {
        setThreasholdForProcessing(THREASHOLD_FOR_ONE_GROUP_PROCESSING);
    }

    @Override
    public void run() {
        while (getConsumer().hasItems()) {
            try {
                if (currentGroupId == null || processedItemsForGroup >= getThreasholdForProcessing()) {
                    changeGroupForProcessing();
                }

                addItemToProcessing(getConsumer().getNextItemByGroupId(currentGroupId));

                for (Item i : processingItems) {
                    processing(i);
                }
            } catch (InterruptedException e) {
                System.err.println(LocalTime.now() + ": [ " + this.getName() + " ]: interrupted");
            }


        }
    }

    private void changeGroupForProcessing() throws InterruptedException {
        System.out.println(LocalTime.now() + ": [ " + this.getName() + " ]: Need to find a new group.");
        if (currentGroupId != null) {
            removeGroupFromProcess(currentGroupId);
        }
        findFreeGroupForProcessing();
        takeGroupToProcess(currentGroupId);
        processedItemsForGroup = 0;
    }

    private void addItemToProcessing(List<Item> items) throws InterruptedException {
        if (!items.isEmpty()) {
            System.out.println(LocalTime.now() + ": [ " + this.getName() + " ] add new item for processing group: " + currentGroupId);
            processingItems.addAll(items);
        } else {
            changeGroupForProcessing();
        }
    }

    private void findFreeGroupForProcessing() {
        List<Long> freeGroups = getConsumer().findFreeGroups();
        while (freeGroups.isEmpty() && getConsumer().hasItems()) {
            try {
                Thread.sleep(200L);
                System.out.println(LocalTime.now() + ": [ " + this.getName() + " ]: TRY TO FIND FREE GROUP....");
                freeGroups = getConsumer().findFreeGroups();
            } catch (InterruptedException e) {
                System.err.println(LocalTime.now() + ": [ " + this.getName() + " ]: interrupted");
            }
        }
        if (freeGroups.isEmpty()) {
            currentGroupId = null;
        } else {
            currentGroupId = freeGroups.get(Utils.generateRandom(0, freeGroups.size() - 1));
        }
    }

    @Override
    public void processing(Item item) {
        try {
            Thread.sleep(100);
            System.out.println(LocalTime.now() + ": [ " + this.getName() + " ] PROCESSING: " + item);

        } catch (InterruptedException e) {
            System.err.println(LocalTime.now() + ": [ " + this.getName() + " ] PROCESSING FAILED: " + item);
            System.err.println(e.getMessage());
        } finally {
            processedItemsForGroup++;
            processingItems.remove(item);
        }

    }

    @Override
    public void takeGroupToProcess(Long groupId) {
        if (currentGroupId != null) {
            getRegistrator().register(Thread.currentThread(), groupId);
            System.out.println(LocalTime.now() + ": [ " + this.getName() + " ] take new group to processing: " + groupId);
        }
    }

    @Override
    public void removeGroupFromProcess(Long groupId) {
        getRegistrator().unregister(Thread.currentThread(), groupId);
        System.out.println(LocalTime.now() + ": [ " + this.getName() + " ] remove group from processing: " + groupId);
    }

    private Consumer<Item> getConsumer() {
        return ConsumerImpl.getInstance();
    }

    private ThreadRegistrator getRegistrator() {
        return ThreadRegistratorImpl.getInstance();
    }

    private int getThreasholdForProcessing() {
        return threasholdForProcessing;
    }

    private void setThreasholdForProcessing(int threasholdForProcessing) {
        this.threasholdForProcessing = threasholdForProcessing;
    }

}
