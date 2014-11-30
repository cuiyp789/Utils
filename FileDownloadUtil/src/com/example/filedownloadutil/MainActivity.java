
package com.example.filedownloadutil;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.ztb.charger.download.Config;
import com.ztb.charger.download.DownloadHelper;
import com.ztb.charger.download.FileManager;
import com.ztb.charger.download.FileStateListener;
import com.ztb.charger.download.NetFile;
import com.ztb.charger.download.UrlUtil;

public class MainActivity extends Activity {
    Activity act;
    ListView listview;
    DownloadAdapter adapter;
    List<NetFile> list = new ArrayList<NetFile>();

    String[] testUrls = {
            "http://apk.r1.market.hiapk.com/data/upload/apkres/2014/11_19/15/com.digitalchocolate.rollnyfullpa_033831.apk",
            "http://tfs.alipayobjects.com/L1/71/100/and/alipay_wap_main.apk",
            "http://zjcmpp.hexin.com.cn/download/mobile/ths_android_V8_72_01.apk"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        act = this;
        
        Config.getInstance().initFolder();
        FileManager.getInstance(getApplicationContext()).addStateListener(lsnr);

        listview = (ListView) findViewById(R.id.listview);
        adapter = new DownloadAdapter(this, listview, list);
        listview.setAdapter(adapter);

        Button btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(list.size()<testUrls.length){
                    NetFile file = new NetFile();
                    file.url = testUrls[list.size()];
                    list.add(file);
                    adapter.notifyDataSetChanged();
                    DownloadHelper.options(act, file);
                }
            }
        });
        
    }

    void testDownload() {
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
                    Log.d("test", "stateChanged, " + info.state);
                    adapter.updateProgress(info);
                }
            });
        }
    };
}
