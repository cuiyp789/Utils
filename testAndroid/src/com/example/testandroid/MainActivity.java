
package com.example.testandroid;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.os.Bundle;
import android.util.Base64InputStream;
import android.util.Base64OutputStream;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        testBase64();
    }

    void testBase64() {
        String path = "/sdcard/test.txt";
        encode(path);
        decode(path + ".base64_encode");
    }
    void encode(String path) {
        try {
            File outFile = new File(path + ".base64_encode");
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
    }
    void decode(String path) {
        try {
            File outFile = new File(path + ".base64_decode");
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
    }
}
