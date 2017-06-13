package com.nagornaja;

import java.util.Random;

/**
 * Created by Elene on 31.05.17.
 */
public class Utils {

    static long generateRandomId(){
        int minIdBound = 1000000;
        int maxIdBound = 9999999;
        return (long) generateRandom(minIdBound, maxIdBound);
    }

    static long generateRandomId(int minIdBound, int maxIdBound){
        return (long) generateRandom(minIdBound, maxIdBound);
    }

    public static int generateRandom(int min, int max){
        return new Random().nextInt(max - min + 1) + min;
    }

}
