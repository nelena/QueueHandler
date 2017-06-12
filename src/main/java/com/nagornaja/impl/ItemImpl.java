package com.nagornaja.impl;

import com.nagornaja.api.Item;

import java.util.Objects;

/**
 * Created by Elene on 28.03.17.
 */
public class ItemImpl implements Item{

    private Long itemId;
    private Long groupId;

    public ItemImpl(Long itemId, Long groupId) {
        this.itemId = itemId;
        this.groupId = groupId;
    }

    public ItemImpl(long itemId, long groupId) {
        this.itemId = itemId;
        this.groupId = groupId;
    }

    public Long getItemId() {
        return itemId;
    }

    public Long getGroupId() {
        return groupId;
    }

    @Override
    public String toString(){
        return "[ GROUP: " + groupId +" ] [ " + itemId + " ]";
    }

    @Override
    public int compareTo(Item o) {
        if(o == null || o.getGroupId() == null || o.getItemId() == null){
            return 1;
        }
        else if(Objects.equals(this.getGroupId(), o.getGroupId())){
            return this.getItemId().compareTo(o.getItemId());
        }else {
            return this.getGroupId().compareTo(o.getGroupId());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(itemId, item.getItemId()) &&
                Objects.equals(groupId, item.getGroupId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId, groupId);
    }

}
