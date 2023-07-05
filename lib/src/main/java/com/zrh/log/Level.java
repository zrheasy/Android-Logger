package com.zrh.log;

/**
 * @author zrh
 * @date 2023/7/5
 */
public enum Level {
    INFO("I"), DEBUG("D"), WARN("W"), ERROR("E");

    private final String name;

    Level(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
