package com.nagornaja.api;

/**
 * Created by Elene on 04.06.17.
 */
public interface SubQueue<I> {

    void lock();

    void unlock();

    Long getGroupId();
}
