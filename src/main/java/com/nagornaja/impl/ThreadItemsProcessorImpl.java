package com.nagornaja.impl;

import com.nagornaja.api.*;

import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Elene on 12.06.17.
 */
public class ThreadItemsProcessorImpl extends Thread implements ThreadItemsProcessor<Item>{

    private static final int THREASHOLD_FOR_ONE_GROUP_PROCESSING = 20;

    private Long currentGroupId;
    private int threasholdForProcessing;
    private int processedItemsForGroup;
    private List<Item> processingItems = new LinkedList<>();

    public ThreadItemsProcessorImpl(){
        setThreasholdForProcessing(THREASHOLD_FOR_ONE_GROUP_PROCESSING);
    }

    @Override
    public void run() {
        while (true){
            try {
                if(currentGroupId == null || processedItemsForGroup >= getThreasholdForProcessing()){
                    changeGroupForProcessing();
                }

                addNewBatchToProcessing(getConsumer().getNextItemsByGroupId(currentGroupId));

                for(Item i : processingItems){
                    processing(i);
                }
            }catch (InterruptedException e){
                System.err.println(LocalTime.now() + ": [ " + this.getName() + " ]: interrupted");
            }


        }
    }

    private void changeGroupForProcessing() throws InterruptedException{
        System.out.println(LocalTime.now() + ": [ " + this.getName() + " ]: Need to find a new group.");
        if(currentGroupId != null){
            removeGroupFromProcess(currentGroupId);
        }
        currentGroupId = findFreeGroupForProcessing();
        takeGroupToProcess(currentGroupId);
        processedItemsForGroup = 0;
    }

    private void addNewBatchToProcessing(List<Item> items){
        System.out.println(LocalTime.now() + ": [ " + this.getName() + " ] add new batch for processing group: " + currentGroupId);
        processingItems.addAll(items);
    }

    private Long findFreeGroupForProcessing() {
        Long groupId = getConsumer().findFreeGroup();
        while (groupId == null) {
            try {
                Thread.sleep(2000L);
                System.err.println(LocalTime.now() + ": [ " + this.getName() + " ]: TRY TO FIND FREE GROUP....");
                groupId = getConsumer().findFreeGroup();
            } catch (InterruptedException e) {
                System.err.println(LocalTime.now() + ": [ " + this.getName() + " ]: interrupted");
            }
        }
        return groupId;
    }

    @Override
    public void processing(Item item) {
        try {
            Thread.sleep(100);
            System.out.println(LocalTime.now() + ": [ " + this.getName() + " ] PROCESSING: " + item);

        }catch (InterruptedException e){
            System.err.println(LocalTime.now() + ": [ " + this.getName() + " ] PROCESSING FAILED: " + item);
            System.err.println(e.getMessage());
        }finally {
            processedItemsForGroup++;
            processingItems.remove(item);
        }

    }

    @Override
    public void takeGroupToProcess(Long groupId) {
        getRegistrator().register(Thread.currentThread(), groupId);
        System.out.println(LocalTime.now() + ": [ " + this.getName() + " ] take new group to processing: " + groupId);
    }

    @Override
    public void removeGroupFromProcess(Long groupId) {
        getRegistrator().unregister(Thread.currentThread(), groupId);
        getConsumer().removeProcessedItemsByGroupId(groupId);
        System.out.println(LocalTime.now() + ": [ " + this.getName() + " ] remove group from processing: " + groupId);
    }

    private Consumer<Item> getConsumer(){
        return ConsumerImpl.getInstance();
    }

    private ThreadRegistrator getRegistrator(){
        return ThreadRegistratorImpl.getInstance();
    }

    private int getThreasholdForProcessing() {
        return threasholdForProcessing;
    }

    private void setThreasholdForProcessing(int threasholdForProcessing) {
        this.threasholdForProcessing = threasholdForProcessing;
    }

}
