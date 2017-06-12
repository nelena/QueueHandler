package com.nagornaja.impl;

import com.nagornaja.Utils;
import com.nagornaja.api.Item;
import com.nagornaja.api.Producer;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AppImpl
{

    public static void main(String[] args) {

        System.out.println(LocalTime.now() + ": Start");


        Producer<Item> producer = new ProducerImpl();
        int threadsCount = Utils.generateRandom(1, producer.getGroupsCount() - 1);
        Thread producerRunner = new Thread(producer);

        producerRunner.start();

        List<ThreadItemsProcessorImpl> threadProcessors = new ArrayList<>();
        for (int i = 0; i < threadsCount; i++){
            threadProcessors.add(new ThreadItemsProcessorImpl());
        }

        threadProcessors.forEach(ThreadItemsProcessorImpl::start);

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

}
