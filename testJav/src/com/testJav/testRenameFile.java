/**
 * testJav
 * com.testJav
 * testRenameFile.java
 * 责任人:  
 * 创建/修改时间: 2014年11月17日-下午6:23:43
 * Copyright © 2013-2014 深圳掌通宝科技有限公司-版权所有
 */
package com.testJav;

import java.io.File;

/**
 * @author Administrator
 *
 */
public class testRenameFile {

    /**
     * @param args
     */
    public static void main(String[] args) {
        String path1 = "d:/test1.txt";
        String path2 = "d:/test2.txt";
        File file1 = new File(path1);
        File file2 = new File(path2);
        System.out.println(file1.getAbsolutePath()+", len: "+file1.length());
        System.out.println(file2.getAbsolutePath()+", len: "+file2.length());
        file2.delete();
        boolean isOk = file1.renameTo(file2);
        System.out.println(isOk);
        System.out.println(file1.getAbsolutePath()+", len: "+file1.length());
        System.out.println(file2.getAbsolutePath()+", len: "+file2.length());
        
    }

}
