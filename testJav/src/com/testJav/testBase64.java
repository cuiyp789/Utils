/**
 * testJav
 * testJav
 * testBase64.java
 * 责任人:  
 * 创建/修改时间: 2014年11月14日-下午5:37:57
 * Copyright © 2013-2014 深圳掌通宝科技有限公司-版权所有
 */
package com.testJav;

import java.io.File;
import java.util.Base64;

import testJav.utils.Base64McUtil;
import testJav.utils.MD5Util;




/**
 * @author Administrator
 *
 */
public class testBase64 {
    public static void main(String[] args) {
        String path = "d:/test.txt";
        Base64McUtil.ecodeFile(new File(path));
        Base64McUtil.decodeFile(new File(path));
        Base64McUtil.decodeFile(new File(path));
        
        System.out.println("end of main()");
    }
}
