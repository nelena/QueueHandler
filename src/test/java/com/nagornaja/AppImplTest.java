package com.nagornaja;


import com.nagornaja.impl.AppImpl;
import org.junit.Test;

/**
 * Unit test for simple AppImpl.
 */
public class AppImplTest
{
   @Test
   public void appTest(){
       String[] args = new String[] {"3", "2", "20"};
       AppImpl.main(args);
   }

   @Test
    public void generateItemsTest(){

   }
}
