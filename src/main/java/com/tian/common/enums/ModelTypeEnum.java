package com.tian.common.enums;

public enum ModelTypeEnum {

    /**
     * 表单类型
     */
    TABLE("1"),
    /**
     * 模型类型
     */
    MODEL("2"),
    /**
     * 脚本类型
     */
    SCRIPT("3");

    private String code;

    private ModelTypeEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    public static ModelTypeEnum getEnum(String value) {
        for (ModelTypeEnum modelTypeEnum : ModelTypeEnum.values()) {
            if (modelTypeEnum.code.equals(value)){
                return modelTypeEnum;
            }
        }
        throw new RuntimeException("Error: Invalid ModelTypeEnum type value: " + value);
    }
}
