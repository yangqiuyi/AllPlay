
package com.example.util.log;

import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;


public class LogUtil {

    public static int LOG_LEVEL = Log.DEBUG;//Log.ERROR;
    public static final String PREFIX = "_LogUtil_|";
    public static void setLogLevel(int level){
        LOG_LEVEL = level;
    }
    public static void addLogNotify(LogNotify logNotify){
        if (_logNotifys == null){
            _logNotifys = new LinkedList<>();
        }
        _logNotifys.add(logNotify);
    }
    public static void removeLogNotify(LogNotify logNotify)
    {
        _logNotifys.remove(logNotify);
        if (_logNotifys.isEmpty()){
            _logNotifys = null;
            onLog(Log.DEBUG,PREFIX,"removeLogNotify,all clear");
        }
    }
    static LogFile _LogFile;
    static List<LogNotify> _logNotifys = new LinkedList<>();
    public static void d(String tag,String exp,Object... args)
    {
        if(LOG_LEVEL> Log.DEBUG)return;

        String log = fmt(exp, args);
        onLog(Log.DEBUG, tag, log);

    }
    public static void i(String tag,String exp,Object... args)
    {
        if(LOG_LEVEL> Log.INFO)return;

        String log = fmt(exp, args);

        onLog(Log.INFO, tag, log);
    }

    public static void w(String tag,String exp,Object... args)
    {
        if(LOG_LEVEL> Log.WARN)return;

        String log = fmt(exp, args);
        onLog(Log.WARN, tag, log);
    }

    public static void e(String tag,String exp,Object... args)
    {
        if(LOG_LEVEL> Log.ERROR)return;

        String log = fmt(exp, args);
        onLog(Log.ERROR, tag, log);
    }

    public static void c(String tag,String msg,Exception e)
    {
        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        e.printStackTrace(printWriter);
        Throwable cause = e.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        Log.e(tag, PREFIX + msg + ":" + result);
    }
    public static void r(String tag,String log)
    {
        if(LOG_LEVEL> Log.ERROR)return;

        onLog(Log.DEBUG, tag, log);
    }

    public static String fmt(String exp,Object... args)
    {
        String log = "";
        try {
            log = String.format(exp, args);
        }catch (Exception e){
            if ( LOG_LEVEL <= Log.DEBUG ) {
                Log.e(PREFIX, "format exception,exp,"+ exp+"->" + e.toString());
            }
            return exp;
        }
        return log;
    }

    private static void onLog(int level,String tag,String log)
    {
        if (_logNotifys != null ){
            for (LogNotify item:_logNotifys)
            {
                item.onLog(level,tag,log);
            }
        }
        String fullTag = PREFIX + tag;
        switch (level)
        {
            case Log.INFO:
                Log.i(fullTag, log);
                break;
            case Log.DEBUG:
                Log.d(fullTag, log);
                break;
            case Log.WARN:
                Log.w(fullTag, log);
                break;
            case Log.ERROR:
                Log.e(fullTag, log);
                break;
            case Log.VERBOSE:
                Log.v(fullTag, log);
                break;

        }
    }

    /*notify*/
    public interface LogNotify
    {
        void onLog(int level, String tag, String log);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////LogFile////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    public static void attachLogFile(String path){
        deatchLogFile();
        LogFile log = new LogFile();
        if(log.open(path))
            _LogFile = log;
    }
    public static void deatchLogFile(){
        if(_LogFile != null){
            _LogFile.close();
            _LogFile = null;
        }
    }
    public static boolean canLogToFile(){
        return (_LogFile != null);
    }
    public static void l(String tag,String exp,Object... args){
        if(_LogFile == null)
            return;
        _LogFile.log(tag,exp,args);
    }
}
