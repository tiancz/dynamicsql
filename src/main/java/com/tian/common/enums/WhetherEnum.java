package com.tian.common.enums;

public enum WhetherEnum {
    YES("1", "是"), NO("0", "否");

    private WhetherEnum(String code, String detail) {
        this.code = code;
        this.detail = detail;
    }

    private String code;
    private String detail;

    public String getCode() {
        return this.code;
    }

    public String getDetail() {
        return this.detail;
    }

}
