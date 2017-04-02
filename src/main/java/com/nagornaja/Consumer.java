package com.nagornaja;

import java.time.LocalTime;
import java.util.List;

/**
 * реализация потока-обработчика для очереди группы, которая в свою очередь является подочередью основной очереди
 * Created by Elene on 30.03.17.
 */
public class Consumer extends Thread {

    private QueueService queueService = QueueService.getInstance();

    /**
     * Поток постоянно ищет элементы в очереди для обработки,
     * находит какую-то упорядоченную часть для какой-то группы, последовательно обрабатывает
     */
    @Override
    public void run() {
        System.out.println(LocalTime.now() + ": START: [ " + this.getName() + " ]");
        List<Item> items;
        do {
            items = queueService.getNextSubqueue();
            try {
                for (Item item : items) {
                    processing(item);
                    Thread.sleep(1);
                }
            } catch (InterruptedException e){
                e.printStackTrace();
            } finally {
                if (!items.isEmpty()) {
                    queueService.unlockGroup();
                }
            }
        } while (!items.isEmpty() || (queueService.hasItems() || !queueService.isFinished()));
        System.out.println(LocalTime.now() + ": [ " + this.getName() + " ] FINISHED.");
    }

    private void processing(Item item){
        System.out.println(LocalTime.now() + ": [ " + this.getName() + " ] PROCESSING: " + item);
    }
}