package com.nagornaja;

import com.nagornaja.api.Item;
import com.nagornaja.api.Producer;
import com.nagornaja.impl.ProducerImpl;
import com.nagornaja.impl.ThreadItemsProcessorImpl;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

class AppImpl
{

    private Long groupsCount;


    void init(){
        System.out.println(LocalTime.now() + ": Start");


        Producer<Item> producer = new ProducerImpl(getGroupsCount(), getGroupsCount() * 5);
        int threadsCount = Utils.generateRandom(1, producer.getGroupsCount() - 1);
        Thread producerRunner = new Thread(producer);

        producerRunner.start();

        List<ThreadItemsProcessorImpl> threadProcessors = new ArrayList<>();
        for (int i = 0; i < threadsCount; i++){
            threadProcessors.add(new ThreadItemsProcessorImpl());
        }

        threadProcessors.parallelStream().forEach(ThreadItemsProcessorImpl::start);

        for(ThreadItemsProcessorImpl processor : threadProcessors){
            try{
                processor.join();
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }

        try{
            producerRunner.join();
        }catch (InterruptedException e){
            e.printStackTrace();
        }

        System.out.println(LocalTime.now() + ": Finish");
    }

    private long getGroupsCount() {
        if(groupsCount == null){
            return 5L;
        }else {
            return groupsCount;
        }
    }

    void setGroupsCount(long groupsCount) {
        this.groupsCount = groupsCount;
    }
}
