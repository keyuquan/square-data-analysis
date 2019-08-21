package com.square.source.enums;

/**
 * Created by chenxiaogang on 18/4/25 0025.
 * version: 0.1
 * 系统类型
 */
public enum DataStatusEnum {

    INIT(0,"开始"),
    SUCCESS(1,"成功"),
    ERROR(-1,"失败"),
	ERROR_QUERY(-2, "查询失败"),
	ERROR_IMPORT(-3, "导入失败");

	private Integer code;
    private String name;
    
    DataStatusEnum(Integer code, String name){
    	this.code = code;
        this.name = name;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}
    
}
