package com.tian.common.enums;

/**
 * 排序类型
 */
public enum OrderByEnum {
    ASC("01","ASC"),
    DESC("02","DESC");

    private OrderByEnum(String code,String detail){
        this.code=code;
        this.detail= detail;
    }

    private String code;
    private String detail;

    public static OrderByEnum getByCode(String code) {
        for (OrderByEnum orderByEnum : values()) {
            if (orderByEnum.getCode().equals(code)) {
                return orderByEnum;
            }
        }
        return null;
    }

    public String getCode() {
        return this.code;
    }

    public String getDetail() {
        return this.detail;
    }
}
