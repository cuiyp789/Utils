/**
 * ZTB.Charger.V1.X
 * com.ztb.charger.utils
 * Base64Util.java
 * 责任人:  
 * 创建/修改时间: 2014年11月14日-下午1:18:27
 * Copyright © 2013-2014 深圳掌通宝科技有限公司-版权所有
 */

package testJav.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.mc.secret.BASE64Decoder;
import com.mc.secret.BASE64Encoder;

/**
 * @author Administrator
 */
public class Base64Util {
    static final String TAG = "Base64Util";

    /**
     * 将整个文件进行base64编码，再写回原文件
     * @param file
     */
    public static void ecodeFile(File file) {
        if (file == null || !file.exists()) {
            //Log.e(TAG, "base64Eecode, no file");
            return;
        }
        File outFile = new File(file.getAbsoluteFile() + ".bak");
        try {
            InputStream inStream = new FileInputStream(file);
            OutputStream outStream = new FileOutputStream(outFile);
            BASE64Encoder encoder = new BASE64Encoder();
            encoder.encode(inStream, outStream);
            inStream.close();
            outStream.close();
            file.delete();
            outFile.renameTo(file);
        } catch (Exception e) {
            //Log.e(TAG, e);
        }
    }

    /**
     * 将整个文件进行base64解码，再写回原文件
     * @param file
     */
    public static void decodeFile(File file) {
        if (file == null || !file.exists()) {
            //Log.e(TAG, "base64Decode, no file");
            return;
        }
        File outFile = new File(file.getAbsoluteFile() + ".bak");
        try {
            InputStream inStream = new FileInputStream(file);
            OutputStream outStream = new FileOutputStream(outFile);
            BASE64Decoder decoder = new BASE64Decoder();
            decoder.decodeBuffer(inStream, outStream);
            inStream.close();
            outStream.close();
            file.delete();
            outFile.renameTo(file);
        } catch (Exception e) {
            //Log.e(TAG, e);
        }
    }
    
    
}
