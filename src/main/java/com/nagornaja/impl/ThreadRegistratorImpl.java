package com.nagornaja.impl;

import com.nagornaja.api.ThreadRegistrator;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Elene on 12.06.17.
 */
public class ThreadRegistratorImpl implements ThreadRegistrator {

    private static volatile ThreadRegistratorImpl instance;
    private Map<Thread, Long> mapOfProcessingGroups = new ConcurrentHashMap<>();

    static ThreadRegistratorImpl getInstance() {
        if (instance == null) {
            synchronized (ThreadRegistratorImpl.class) {
                if (instance == null) {
                    instance = new ThreadRegistratorImpl();
                }
            }
        }
        return instance;
    }


    @Override
    public void register(Thread thread, Long groupId) {
        mapOfProcessingGroups.put(thread, groupId);
    }

    @Override
    public void unregister(Thread thread, Long groupId) {
        mapOfProcessingGroups.remove(thread);
    }

    @Override
    public Set<Long> getProcessingGroupIds(){
        return new HashSet<>(mapOfProcessingGroups.values());
    }
}
