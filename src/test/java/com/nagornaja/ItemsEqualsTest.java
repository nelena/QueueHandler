package com.nagornaja;

import com.nagornaja.api.Item;
import com.nagornaja.impl.ItemImpl;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by Elene on 31.05.17.
 */
public class ItemsEqualsTest {

    @Test
    public void itemsEqualsTest(){


        for(int i = 0; i < 1000; i++){
            long itemId = Utils.generateRandomId();
            long groupId = Utils.generateRandomId(1, 10);
            Item item1 = new ItemImpl(itemId, groupId);
            Item item2 = new ItemImpl(itemId, groupId);
            Assert.assertEquals(item1, item2);
            Assert.assertEquals(item1.hashCode(), item2.hashCode());
        }
    }


}
