package com.nagornaja.api;

import java.util.Set;

/**
 * Created by Elene on 06.06.17.
 */
public interface ThreadRegistrator {

    void register(Thread thread, Long groupId);

    void unregister(Thread thread, Long groupId);

    Set<Long> getProcessingGroupIds();
}
