package com.nagornaja;

import com.nagornaja.api.Item;
import com.nagornaja.impl.ItemImpl;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Elene on 31.05.17.
 */
public class ItemsCompareTest {

    @Test
    public void itemsCompareByIDTest(){
        for(int i = 0; i < 1000; i++){
            long itemId_1 = Utils.generateRandomId(100, 200);
            long itemId_2 = Utils.generateRandomId(300, 400);
            Item item1 = new ItemImpl(itemId_1, 1);
            Item item2 = new ItemImpl(itemId_2, 1);
            Item item3 = new ItemImpl(itemId_1, 1);
            Assert.assertEquals(item1.compareTo(item2), -1);
            Assert.assertEquals(item2.compareTo(item1),  1);
            Assert.assertEquals(item1.compareTo(item3),  0);
        }
    }

    @Test
    public void itemsCompareByGroupTest(){
        for(int i = 0; i < 1000; i++){
            long groupId_1 = Utils.generateRandomId(100, 200);
            long groupId_2 = Utils.generateRandomId(300, 400);
            Item item1 = new ItemImpl(1, groupId_1);
            Item item2 = new ItemImpl(2, groupId_2);
            Item item3 = new ItemImpl(1, groupId_1);
            Assert.assertEquals(item1.compareTo(item2), -1);
            Assert.assertEquals(item2.compareTo(item1),  1);
            Assert.assertEquals(item1.compareTo(item3),  0);
        }
    }
}
