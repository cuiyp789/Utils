/**
 * testJav
 * testJav
 * testInputStream.java
 * 责任人:  
 * 创建/修改时间: 2014年11月13日-下午5:49:48
 * Copyright © 2013-2014 深圳掌通宝科技有限公司-版权所有
 */
package testJav;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Administrator
 *
 */
public class testInputStream {
    public static void main(String[] args) {
        try{
            String path = "d:/test";
            File f = new File(path);
            String str1 = "hello, world!";
            String str2 = "I am here.";
//            OutputStream os = new FileOutputStream(f);
//            os.write((str1+str2).getBytes());
            
            InputStream is = new FileInputStream(f);
            byte[] buf = new byte[1024]; 
            
            is.mark(10);
            is.skip(str1.getBytes().length);
            int len = is.read(buf, 0, str2.getBytes().length);
            System.out.println(new String(buf, 0, len));
            
            is.reset();
            is.skip(str1.getBytes().length);
            len = is.read(buf, 0, str2.getBytes().length);
            System.out.println(new String(buf, 0, len));
            
            is.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        System.out.println("end of main()");
    }
}
