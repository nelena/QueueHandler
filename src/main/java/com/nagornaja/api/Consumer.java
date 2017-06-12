package com.nagornaja.api;

import java.util.List;

/**
 * Created by Elene on 31.05.17.
 */
public interface Consumer<I>{
    List<I> getNextItems();

    List<I> getNextItemsByGroupId(Long groupId);

    Long findFreeGroup();

    void removeProcessedItemsByGroupId(Long groupId);
}
