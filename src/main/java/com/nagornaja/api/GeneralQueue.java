package com.nagornaja.api;

import com.nagornaja.impl.SubQueueImpl;

import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by Elene on 31.05.17.
 */
public interface GeneralQueue<I> {


    SubQueueImpl createNotExistingSubqueue(Long groupId);


    List<I> getNextItems();

    List<I> getNextItemsByGroupId(Long groupId);

    void putItem(I item);

    boolean hasItems();

    void removeProcessedItemsByGroupId(Long groupId);

    Long getFreeGroupId();

    Set<Long> getAllGroupIds();

    void setIsAddingFinished(boolean isAddingFinished);

    boolean getIsAddingFinished();

}
