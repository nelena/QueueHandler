package com.nagornaja;

import java.time.LocalTime;
import java.util.Random;

/**
 * Класс, отвечающий за асинхронное произвдство объектов и добавление в очередь
 * Created by Elene on 31.03.17.
 */
public class Producer extends Thread{
    private int groupsCount;
    private int itemsCount;

    private QueueService queueService = QueueService.getInstance();

    public Producer(int groupsCount, int itemsCount){
        this.groupsCount = groupsCount;
        this.itemsCount = itemsCount;
    }
    @Override
    public void run() {
        Random random = new Random();
        for(int i = 0; i < itemsCount; i++) {
            Item item = new Item(random.nextInt(itemsCount), random.nextInt(groupsCount));
            System.out.println(LocalTime.now() + ": CREATE ITEM: " + item);
            queueService.putItem(item);
            try {
                Thread.sleep(1);
            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        queueService.finish();
        System.out.println(LocalTime.now() + ": ADDING FINISHED.");
    }
}
