package com.example.util.log;


import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogFile {
    public static final String TAG = "LogFile";
    private BufferedWriter _logWriter;
    private SimpleDateFormat _dateFmt;
    private String _logPath;
    private long _maxSize = 4 * 1024 * 1024;
    public boolean open(String path){
       return open(path,true);
    }
    public boolean open(String path,boolean append){
        try {
            File log = new File(path);
            if(!log.exists() || !append){
                Log.d(TAG, "open,create new log:" + path);
                log.createNewFile();
            }else{
                if(_maxSize > 0 && log.length() >= _maxSize) {
                    Log.d(TAG, "open,log:" + path + "size:" + log.length() + "create new one");
                    log.createNewFile();
                }
            }

            _dateFmt = new SimpleDateFormat("[yy-MM-dd hh:mm:ss] :");
            FileWriter writer = new FileWriter(log);
            _logWriter = new BufferedWriter(writer);
            _logPath = path;
            Log.d(TAG, "log," + _logPath + ",opened...");
            return true;
        }catch (Exception e){}
        return false;
    }
    public void setMaxFileSize(long size){
        _maxSize = size;
        if(_maxSize <= 0 )
            return;
        if(_logPath != null){
            File log = new File(_logPath);
            long length = log.length();
            if(length >= size){
                close();
                open(_logPath,false);
            }
        }
    }

    public void close(){
        if(_logWriter == null)
            return;
        try {
            _logWriter.close();
        }catch (Exception e){}
        _logWriter = null;
    }
    public void log(String tag,String log,Object... args){
        if(_logWriter == null)
            return;

        try {
            String msg = String.format(log, args);
            _logWriter.write(_dateFmt.format(new Date()));
            _logWriter.write(tag);
            _logWriter.write(",");
            _logWriter.write(msg);
            _logWriter.write("\n");
            _logWriter.flush();
        }catch (Exception e){}
    }
}
