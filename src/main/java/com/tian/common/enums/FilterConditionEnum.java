package com.tian.common.enums;

/**
 * 过滤条件
 */
public enum FilterConditionEnum {

    /**
     * 等于
     */
    EQUAL("01", "="),
    /**
     * 大于
     */
    GREATER("02", ">"),
    /**
     * 小于
     */
    LESS("03", "<"),
    /**
     * 不等于
     */
    NOT_EQUAL("04", "!="),
    /**
     * 小于等于
     */
    LESS_OR_EQUAL("05", "<="),
    /**
     * 大于等于
     */
    GREATER_OR_EQUAL("06", ">="),
    /**
     * 包含
     */
    CONTAIN("07", "包含"),
    /**
     * 不包含
     */
    NOT_CONTAIN("08", "不包含");

    private String code;
    private String message;

    FilterConditionEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }


}
