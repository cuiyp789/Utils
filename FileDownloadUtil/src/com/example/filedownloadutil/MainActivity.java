
package com.example.filedownloadutil;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import org.apache.http.client.utils.URIUtils;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.URLUtil;

import com.ztb.charger.download.Config;
import com.ztb.charger.download.NetFile;
import com.ztb.charger.download.DownloadHelper;
import com.ztb.charger.download.FileManager;
import com.ztb.charger.download.FileStateListener;
import com.ztb.charger.download.UrlUtil;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        testDownload();
    }

    void testDownload(){
        String url = "http://192.168.1.184:9000/install/app/20141120/1947aa5011140075ff710fdff9ffe99e/百度应用.apk?a = 3&b = 4";
        Config.getInstance().initFolder();
        FileManager.getInstance(getApplicationContext()).addStateListener(lsnr);
        NetFile file = new NetFile();
        file.url = UrlUtil.getEncodedUrl(url);
        DownloadHelper.options(this, file);
    }

    private FileStateListener lsnr = new FileStateListener() {

        @Override
        public void stateChanged(final NetFile info, final Context context) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("test", "stateChanged, "+info.state);
                }
            });
        }
    };
}
