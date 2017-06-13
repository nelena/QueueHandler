package com.nagornaja.api;

import java.util.List;

/**
 * Created by Elene on 31.05.17.
 */
public interface Consumer<I>{

    List<I> getNextItemByGroupId(Long groupId);

    List<Long> findFreeGroups();

    boolean hasItems();

}
