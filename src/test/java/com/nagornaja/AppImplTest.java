package com.nagornaja;


import org.junit.Test;

/**
 * Unit test for simple AppImpl.
 */
public class AppImplTest
{

   @Test
    public void AppTest1(){
        AppImpl app = new AppImpl();
        app.setGroupsCount(7);
        app.init();
   }

    @Test
    public void AppTest2(){
        AppImpl app = new AppImpl();
        app.setGroupsCount(15);
        app.init();
    }
}
