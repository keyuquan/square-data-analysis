package com.square.common.enums;

import com.square.common.Constants;

/**
 * 预置的事件类型枚举类
 */
public enum EventMappingCode {

    OWNER_ALL(Constants.OWNER_ALL_TYPE, Constants.OWNER_MAPPING),
    SK_BILL("sk_bill", Constants.SK_BILL_MAPPING),
    SK_BILL_RECHARGE("sk_bill_recharge", Constants.SK_BILL_MAPPING),
    SK_REDBONUS("sk_redbonus", Constants.SK_REDBONUS_MAPPING),
    SK_REDBONUS_GRAB("sk_redbonus_grab", Constants.SK_REDBONUS_GRAB_MAPPING),
    SK_FINANCE_CASH_DRAWS("sk_finance_cash_draws", Constants.SK_FINANCE_CASH_DRAWS_MAPPING),
    SK_FINANCE_RECHARGE("sk_finance_recharge", Constants.SK_FINANCE_RECHARGE_MAPPING),
    SK_USER_BASEINFO("sk_user_baseinfo", Constants.SK_USER_BASEINFO_MAPPING),
    ST_DAY(Constants.ST_DAY_TYPE, Constants.ST_DAY_MAPPING);

    private String value;

    private String mapping;

    EventMappingCode(String value, String mapping) {
        this.value = value;
        this.mapping = mapping;
    }


    /**
     * 根据value 获取名字
     *
     * @param value
     * @return
     */
    public static String getMapping(String value) {
        EventMappingCode[] values = EventMappingCode.values();
        for (EventMappingCode dataType : values) {
            if (dataType.getValue().equals(value)) {
                return dataType.getMapping();
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

    public String getMapping() {
        return mapping;
    }

    public void setMapping(String mapping) {
        this.mapping = mapping;
    }


}
