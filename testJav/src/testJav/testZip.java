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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import testJav.utils.Base64Util;
import testJav.utils.ZipUtil;

import com.mc.secret.BASE64Encoder;


/**
 * @author Administrator
 *
 */
public class testZip {
    public static void main(String[] args) {
        try{
            String path1 = "d:/test.txt";
            String path2 = "d:/新建文本文档.txt";
            String zipPath = "d:/test.zip";
            List<File> resFileList = new ArrayList<File>(); 
            resFileList.add(new File(path1));
            resFileList.add(new File(path2));
            File zipFile = new File(zipPath);
            ZipUtil.zipFiles(resFileList, zipFile);
        }catch(Exception e){
            e.printStackTrace();
        }
        System.out.println("end of main()");
    }
}
