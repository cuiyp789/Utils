/**
 * testJav
 * testJav
 * testBase64.java
 * 责任人:  
 * 创建/修改时间: 2014年11月14日-下午5:37:57
 * Copyright © 2013-2014 深圳掌通宝科技有限公司-版权所有
 */
package testJav;

import java.io.File;
import java.util.Base64;

import testJav.utils.Base64Util;

import com.mc.secret.BASE64Encoder;


/**
 * @author Administrator
 *
 */
public class testBase64 {
    public static void main(String[] args) {
        String path = "d:/test.txt";
        Base64Util.ecodeFile(new File(path));
//        Base64Util.decodeFile(new File(path));
//        Base64Util.decodeFile(new File(path));
        System.out.println("end of main()");
    }
}
