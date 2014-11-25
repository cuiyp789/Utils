
package com.example.testandroid;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import com.example.testandroid.utils.Base64McUtil;
import com.example.testandroid.utils.Base64Util;

import android.app.Activity;
import android.os.Bundle;
import android.util.Base64InputStream;
import android.util.Base64OutputStream;
import android.util.Log;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        testBase64();
    }

    void testBase64() {
        String path = "/sdcard/test.zip";
//        File encodeFile = Base64Util.encodeFile(path);
        File decodeFile = Base64Util.decodeFile(path);
//        Log.d("test", "encodeFile: "+encodeFile.getAbsolutePath());
        Log.d("test", "decodeFile: "+decodeFile.getAbsolutePath());
//        Base64McUtil.ecodeFile(new File(path));
//        Base64McUtil.decodeFile(new File(path));
        Log.d("test", "test end");
    }
}
