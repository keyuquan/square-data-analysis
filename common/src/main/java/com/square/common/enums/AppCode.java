package com.square.common.enums;

/**
 * 预置的事件类型枚举类
 */
public enum AppCode {
    REDBONUS("redbonus", "红包");

    private String value;
    private String name;

    AppCode(String value, String name) {
        this.value = value;
        this.name = name;
    }

    /**
     * 根据 name 获得value
     *
     * @param name
     * @return
     */
    public static String getValue(String name) {
        AppCode[] values = AppCode.values();
        for (AppCode dataType : values) {
            if (dataType.getName().equals(name)) {
                return dataType.getValue();
            }
        }
        return null;
    }

    /**
     * 根据value 获取名字
     *
     * @param value
     * @return
     */
    public static String getName(String value) {
        AppCode[] values = AppCode.values();
        for (AppCode dataType : values) {
            if (dataType.getValue().equals(value)) {
                return dataType.getName();
            }
        }
        return null;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
