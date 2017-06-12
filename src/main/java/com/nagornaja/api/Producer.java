package com.nagornaja.api;

/**
 * Created by Elene on 31.05.17.
 */
public interface Producer<I> extends Runnable{

    void putItem(I item);

    int getGroupsCount();
}
