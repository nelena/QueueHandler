package com.nagornaja.api;

/**
 * Created by Elene on 06.06.17.
 */
public interface ThreadItemsProcessor<I> {

    void processing(I item);

    void takeGroupToProcess(Long groupId);

    void removeGroupFromProcess(Long groupId);

}
