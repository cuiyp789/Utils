/**
 * testJav
 * com.testJav
 * testDate.java
 * 责任人:  
 * 创建/修改时间: 2014年11月20日-下午2:34:23
 * Copyright © 2013-2014 深圳掌通宝科技有限公司-版权所有
 */
package com.testJav;

import java.util.Calendar;

/**
 * @author Administrator
 *
 */
public class testDate {

    /**
     * @param args
     */
    public static void main(String[] args) {
        long t1 = System.currentTimeMillis();
        t1 -= 24*60*60*1000;
        System.out.println(t1+", "+isToday(t1));
    }

    public static boolean isToday(long millionSec){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millionSec);
        int y1 = cal.get(Calendar.YEAR);
        int m1 = cal.get(Calendar.MONTH);
        int d1 = cal.get(Calendar.DAY_OF_MONTH);
        cal.setTimeInMillis(System.currentTimeMillis());
        int y2 = cal.get(Calendar.YEAR);
        int m2 = cal.get(Calendar.MONTH);
        int d2 = cal.get(Calendar.DAY_OF_MONTH);
        return y1 == y2 && m1 == m2 && d1 == d2;
    }
}
