package com.square.source.enums;

public enum DatabaseTypeEnum {

	MYSQL("mysql", "mysql"),
	ES("es", "elasticsearch"),
	MONGO("mongo", "mongodb");
	
	private String code;
    private String name;
    
    DatabaseTypeEnum(String code, String name){
    	this.code = code;
        this.name = name;
    }

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
    
    
	
}
