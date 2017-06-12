package com.nagornaja.api;

import com.nagornaja.impl.ItemImpl;

import java.util.Objects;

/**
 * Created by Elene on 31.05.17.
 */
public interface Item extends Comparable<Item> {

    public Long getItemId();
    public Long getGroupId();

}
