package com.nagornaja.api;

/**
 * Created by Elene on 31.05.17.
 */
public interface Item extends Comparable<Item> {

    Long getItemId();
    Long getGroupId();

}
