package com.zrh.log;

import android.annotation.SuppressLint;
import android.util.Log;

/**
 * @author zrh
 * @date 2023/7/5
 */
public class LogcatPrinter implements Printer {
    private final boolean enable;

    public LogcatPrinter(boolean enable) {
        this.enable = enable;
    }

    @SuppressLint("WrongConstant")
    @Override
    public void println(Level level, String tag, String msg) {
        int priority;
        switch (level) {
            case DEBUG: {
                priority = Log.DEBUG;
                break;
            }
            case WARN: {
                priority = Log.WARN;
                break;
            }
            case ERROR: {
                priority = Log.ERROR;
                break;
            }
            default: {
                priority = Log.INFO;
            }
        }
        Log.println(priority, tag, msg);
    }

    @Override
    public boolean isEnable() {
        return enable;
    }
}
