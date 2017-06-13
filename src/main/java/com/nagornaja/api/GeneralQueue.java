package com.nagornaja.api;

import java.util.List;
import java.util.Set;

/**
 * Created by Elene on 31.05.17.
 */
public interface GeneralQueue<I> {


    void createNotExistingSubqueue(Long groupId);

    List<Item> getNextItemByGroupId(Long groupId);

    void putItem(I item);

    boolean hasItems();

    void removeProcessedGroup(Long groupId);

    Set<Long> getAllGroupIds();

    void setIsAddingFinished();

    boolean getIsAddingFinished();

    boolean isFinished();

}
