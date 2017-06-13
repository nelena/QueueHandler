package com.nagornaja.impl;

import com.nagornaja.api.Item;
import com.nagornaja.api.SubQueue;

import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by Elene on 04.06.17.
 */
public class SubQueueImpl extends PriorityBlockingQueue<Item> implements SubQueue{

    private Long groupId;

    @Override
    public boolean add(Item item) {
        if(groupId == null){
            groupId = item.getGroupId();
        }
        return super.add(item);
    }


    Item getNextItemForProcessing(){
        return this.poll();
    }




    public Long getGroupId() {
        if(groupId != null){
            return groupId;
        }else throw new IllegalArgumentException();
    }

}
