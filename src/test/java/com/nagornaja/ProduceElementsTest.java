package com.nagornaja;

import com.nagornaja.api.Item;
import com.nagornaja.api.Producer;
import com.nagornaja.impl.ItemImpl;
import com.nagornaja.impl.ProducerImpl;
import org.junit.Test;


/**
 * Created by Elene on 12.06.17.
 */
public class ProduceElementsTest {

    @Test
    public void produceNormalElementsTest(){
        Producer<Item> producer = new ProducerImpl();
        Long[] groups = new Long[5];
        int groupCount = 5;
        for(int k = 0; k < groupCount; k++){
            groups[k] = Utils.generateRandomId();
            System.out.println(groups[k]);
        }
        for (int i = 0; i < 100; i++) {
            producer.putItem(new ItemImpl(Long.valueOf(Utils.generateRandomId()), groups[Utils.generateRandom(0, groupCount - 1)]));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void produceIllegalElementsTest(){
        Producer<Item> producer = new ProducerImpl();
        producer.putItem(new ItemImpl(Utils.generateRandomId(), null));
    }
}
