package com.nagornaja;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class App
{
    private final int GROUP_COUNT = 5;
    private final int THREAD_COUNT = 2;
    private final int ITEMS_COUNT = 50;

    private int groupsCount;
    private int threadsCount;
    private int itemsCount;

    private Producer producer;

    private LinkedList<Item> queue = new LinkedList<>();

    public App(){
        new App(GROUP_COUNT, THREAD_COUNT, ITEMS_COUNT);
    }

    public App(int groupsCount, int threadsCount, int itemsCount){
        this.groupsCount = groupsCount;
        this.threadsCount = threadsCount;
        this.itemsCount = itemsCount;

    }

    public static void main(String[] args) {

        System.out.println(LocalTime.now() + ": Start");

        int groupsCount = Integer.decode(args[0]);
        int threadsCount = Integer.decode(args[1]);
        int itemsCount = Integer.decode(args[2]);

        Producer producer = new Producer(groupsCount, itemsCount);
        producer.start();

        List<Consumer> consumers = new ArrayList<>();
        for (int i = 0; i < threadsCount; i++){
            consumers.add(new Consumer());
        }

        consumers.parallelStream().forEach(Consumer::start);

        for(Consumer consumer : consumers){
            try{
                consumer.join();
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }

        try{
            producer.join();
        }catch (InterruptedException e){
            e.printStackTrace();
        }

        System.out.println(LocalTime.now() + ": Finish");

    }

}
