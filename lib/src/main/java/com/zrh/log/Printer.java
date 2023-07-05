package com.zrh.log;

/**
 * @author zrh
 * @date 2023/7/5
 */
public interface Printer {
    void println(Level level, String tag, String msg);

    boolean isEnable();
}
