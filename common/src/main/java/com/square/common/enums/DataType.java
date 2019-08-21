package com.square.common.enums;

/**
 * 数据类型 1-字符串 2-文本 3-整数 4-浮点数 5-日期 6-Boolean
 */
public enum DataType {

    STRING(1, "字符串", "keyword"),
    TEXT(2, "文本", "text"),
    LONG(3, "整数", "long"),
    FLOAT(4, "浮点数", "float"),
    DATE(5, "日期", "datetime"),
    BOOLEAN(6, "Boolean", "boolean");

    private Integer value;
    private String name;
    private String esType;

    DataType(Integer value, String name, String esType) {
        this.value = value;
        this.name = name;
        this.esType = esType;
    }

    public static DataType getDataType(Integer value){
        if (value == null) {
            return null;
        }
        DataType[] values = DataType.values();
        for (DataType dataType : values) {
            if (dataType.getValue().equals(value)) {
                return dataType;
            }
        }
        return null;
    }

    /**
     * 根据 name 获得value
     *
     * @param name
     * @return
     */
    public static Integer getValue(String name) {
        DataType[] values = DataType.values();
        for (DataType dataType : values) {
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
    public static String getName(Integer value) {
        DataType[] values = DataType.values();
        for (DataType dataType : values) {
            if (dataType.getValue().equals(value)) {
                return dataType.getName();
            }
        }
        return null;
    }

    /**
     * 根据value 获取es 数据类型
     *
     * @param value
     * @return
     */
    public static String getEsType(Integer value) {
        DataType[] values = DataType.values();
        for (DataType dataType : values) {
            if (dataType.getValue().equals(value)) {
                return dataType.getEsType();
            }
        }
        return null;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEsType() {
        return esType;
    }

    public void setEsType(String esType) {
        this.esType = esType;
    }
}
