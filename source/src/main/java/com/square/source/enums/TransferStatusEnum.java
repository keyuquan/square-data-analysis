package com.square.source.enums;

public enum TransferStatusEnum {

	NORMAL("normal","正常"),
    RENEW("renew","重传");

	private String code;
    private String name;
    
    TransferStatusEnum(String code, String name){
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
