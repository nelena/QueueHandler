package com.nagornaja;

/**
 * Created by Elene on 28.03.17.
 */
public class Item implements Comparable<Item>{

    private Long itemId;
    private Long groupId;

    public Item(long itemId, long groupId) {
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

    /**
     * Реализуем метод сравнения элементов, чтобы в дальнейшем можно было упорядочивать элементы,
     * что требуется в условии.
     * @param o
     * @return
     */
    @Override
    public int compareTo(Item o) {
        if(o == null || o.groupId == null || o.getItemId() == null){
            return 1;
        }
        else return this.getItemId().compareTo(o.getItemId());
    }
}
