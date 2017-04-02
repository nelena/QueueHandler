package com.nagornaja;

import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Класс, отвечающий за координацию работы очереди между Producer-ом(Производитель) и Consumer-ми(Потребителями)
 * Created by Elene on 31.03.17.
 */
public class QueueService {
    public static volatile QueueService instance;

    private final int amountRestriction = 10;

    private Integer groupsCount;
    /**
     * Пары Thread -> groupId. Соответствует тому, какой поток с какой группой работает в данный момент.
     * Выбрала:
     * ConcurrentHashMap<K, V>   — реализует ConcurrentMap<K, V>   —
     * Интерфейс, расширяющий Map несколькими дополнительными атомарными операциями.
     */
    private ConcurrentHashMap<Thread, Long> mapOfProcessingGroups;
    /**
     * Пары groupId -> Lock. Для блокировки выбрала:
     * ReentrantLock   — Лок на вхождение. Только один поток может зайти в защищенный блок.
     */
    private ConcurrentHashMap<Long, ReentrantLock> mapOfGroupsLocks;
    /**
     * Пары groupId -> Subqueue. Для реализации подпоследовательности выбрала:
     * PriorityBlockingQueue<E>   — Является многопоточной оберткой над PriorityQueue.
     * При вставлении элемента в очередь, его порядок определяется в соответствии с
     * логикой Comparator'а или имплементации Comparable интерфейса у элементов.
     * Первым из очереди выходит самый наименьший элемент.
     * Когда исчерпываем очередь, poll() возвращает null.
     */
    private ConcurrentHashMap<Long, PriorityBlockingQueue<Item>> mapOfSubqueues;
    private AtomicBoolean hasItems;
    private AtomicBoolean finished;


    private QueueService(){
        groupsCount = 0;
        mapOfSubqueues = new ConcurrentHashMap<>();
        mapOfGroupsLocks = new ConcurrentHashMap<>();
        mapOfProcessingGroups = new ConcurrentHashMap<>();
        hasItems = new AtomicBoolean(false);
        finished = new AtomicBoolean(false);
    }

    public static QueueService getInstance(){
        if(instance == null){
            synchronized (QueueService.class){
                if(instance == null){
                    instance = new QueueService();
                }
            }
        }
        return instance;
    }

    /**
     * Поток Consumer обращается за новой "порцией" элементов
     * @return
     */
    public List<Item> getNextSubqueue(){
        List<Item> res = new ArrayList<>();
        Long lastGroupId = mapOfProcessingGroups.get(Thread.currentThread());
        if (lastGroupId == null) {
            lastGroupId = groupsCount - 1L;
        }
        Long groupId = lastGroupId;
        boolean localHasItems = false;
        do {
            groupId = Math.floorMod(groupId + 1, groupsCount);

            PriorityBlockingQueue<Item> queue = mapOfSubqueues.get(groupId);
            ReentrantLock lock = mapOfGroupsLocks.get(groupId);
            System.out.println(LocalTime.now() + ": [ " + Thread.currentThread().getName() + " ] TRY [ GROUP: " + groupId +
                    " ] : IS EMPTY: " + queueIsEmpty(queue));
            localHasItems = localHasItems || !queueIsEmpty(queue);
            if (!queueIsEmpty(queue) && lock.tryLock()) {
                System.out.println(LocalTime.now() + ": [ " + Thread.currentThread().getName() + " ] LOCK [ GROUP: " + groupId + " ]");
                int count = 0;
                while (!queueIsEmpty(queue) && count < amountRestriction) {
                    Item item = queue.poll();
                    if (item != null) {
                        res.add(item);
                        count++;
                    }
                }
                mapOfProcessingGroups.put(Thread.currentThread(), groupId);
            }
        } while (res.isEmpty() && !groupId.equals(lastGroupId));

        if (!localHasItems) {
            hasItems.set(mapOfSubqueues.entrySet().parallelStream().anyMatch((e) -> !e.getValue().isEmpty()));
        }

        return res;
    }

    private boolean queueIsEmpty(Queue<Item> queue){
        return queue == null ? true : queue.isEmpty();
    }

    /**
     * Добавляем элемент в мап очередей, определяем его группу и добавляем в мапу,
     * если в мапе нет такой группы, то создаем новую
     * @param item
     */
    public void putItem(Item item){
        Long groupId = item.getGroupId();
        if(isGroupExists(groupId)){
            addItemToGroup(item);
        }else{
            createNotExistingGroup(groupId);
            addItemToGroup(item);
        }
    }

    public void addItemToGroup(Item item){
        Long groupId = item.getGroupId();
        mapOfSubqueues.get(groupId).add(item);
        System.out.println(LocalTime.now() + ": ITEM [ " + item.getItemId() + " ] ADDED TO [ GROUP: " + groupId + " ]"
                + printSubqueueByGroupId(mapOfSubqueues.get(groupId)));
    }

    public void unlockGroup() {
        Long groupId = mapOfProcessingGroups.get(Thread.currentThread());
        ReentrantLock lock = mapOfGroupsLocks.get(groupId);
        System.out.println(LocalTime.now() + ": [ " + Thread.currentThread().getName() + " ] UNLOCK [ GROUP: " + groupId + " ]");
        lock.unlock();
    }

    public boolean hasItems() {
        return hasItems.get();
    }

    public boolean isFinished(){
        return finished.get();
    }

    public PriorityBlockingQueue<Item> createNotExistingGroup(Long groupId){
        mapOfSubqueues.put(groupId, new PriorityBlockingQueue<Item>());
        mapOfGroupsLocks.put(groupId, new ReentrantLock());
        System.out.println(LocalTime.now() + ": CREATE GROUP: [ GROUP: " + groupId + " ]");
        groupsCount++;
        return mapOfSubqueues.get(groupId);
    }

    public boolean isGroupExists(Long groupId){
        return mapOfSubqueues.containsKey(groupId);
    }

    public String printSubqueueByGroupId(PriorityBlockingQueue<Item> queue){
        StringBuilder res = new StringBuilder();
        for (Item item :queue){
            res.append("[ ").append(item.getItemId()).append(" ] ");
        }
        return res.toString();
    }

    public void finish() {
        finished.set(true);
    }
}