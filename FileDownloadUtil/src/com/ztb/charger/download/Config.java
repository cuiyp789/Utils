package com.ztb.charger.download;

import java.io.File;


public class Config {
    private Config(){}
    private static class Holder{
        static Config INSTANCE = new Config();
    }
    public static Config getInstance(){
        return Holder.INSTANCE;
    }
  
    public String downloadDir = "/sdcard/test/";
    public int TIME_OUT_DOWNLOAD_FILE_IN_MS = 30000;
    
    public final String TMP_FILE_SUFFIX = ".tmp";
    
    public boolean initFolder(){
        try{
            File dir = new File(downloadDir);
            if(!dir.exists()){
                return dir.mkdirs();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
}