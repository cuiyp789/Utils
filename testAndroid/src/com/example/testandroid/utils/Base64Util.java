/**
 * testAndroid
 * com.example.testandroid.utils
 * Base64Util.java
 * 责任人:  
 * 创建/修改时间: 2014年11月17日-下午2:00:52
 * Copyright © 2013-2014 深圳掌通宝科技有限公司-版权所有
 */
package com.example.testandroid.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import android.util.Base64InputStream;
import android.util.Base64OutputStream;

/**
 * @author Administrator
 *
 */
public class Base64Util {
    public static File encodeFile(String path) {
        File outFile = null;
        try {
            outFile = new File(path + ".base64_encode");
            InputStream in = new FileInputStream(new File(path));
            OutputStream out = new FileOutputStream(outFile);
            Base64OutputStream bout = new Base64OutputStream(out, android.util.Base64.DEFAULT);
            int len = 0;
            byte[] buf = new byte[64];
            while ((len = in.read(buf, 0, 64)) != -1) {
                bout.write(buf, 0, len);
            }
            in.close();
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outFile;
    }
    public static File decodeFile(String path) {
        File outFile = null;
        try {
            outFile = new File(path + ".base64_decode");
            InputStream in = new FileInputStream(new File(path));
            Base64InputStream bin = new Base64InputStream(in, android.util.Base64.DEFAULT);
            OutputStream out = new FileOutputStream(outFile);
            int len = 0;
            byte[] buf = new byte[64];
            while ((len = bin.read(buf, 0, 64)) != -1) {
                out.write(buf, 0, len);
            }
            in.close();
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outFile;
    }
}
