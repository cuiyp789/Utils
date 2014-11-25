/**
 * testJav
 * com.testJav
 * testReference.java
 * 责任人:  
 * 创建/修改时间: 2014年11月17日-下午6:10:06
 * Copyright © 2013-2014 深圳掌通宝科技有限公司-版权所有
 */

package com.testJav;

import java.io.File;

/**
 * @author Administrator
 */
public class testReference {

    static class TestObj {
        int val;
    }

    public static void main(String[] args) {
        testReference tester = new testReference();
        TestObj obj = new TestObj();
        obj.val = 1;
        obj = test(obj);
        System.out.println(obj.val);
    }

    static TestObj test(TestObj obj) {
        TestObj obj2 = new TestObj();
        obj2.val = 2;
        obj = obj2;
        return obj2;
    }
}
