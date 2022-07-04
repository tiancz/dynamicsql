package com.tian.common.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * 匹配模式
 */
public enum OperateModelEnum {
    EXACT_MATCH("01", "精确匹配", "{fieldName} = {inputParam}"),
    LEFT_MATCH("02", "左匹配", "{fieldName} like {inputParam}"),
    RIGHT_MATCH("03", "右匹配", "{fieldName} like {inputParam}"),
    FUZZY_QUERY("04", "模糊查询", "{fieldName} like {inputParam}"),
    OPEN_INTERVAL("05", "开区间", "({fieldName} > {start_inputParam} and {fieldName} < {end_inputParam} )"),
    CLOSED_INTERVAL("06", "闭区间", "({fieldName} >= {start_inputParam} and {fieldName} <= {end_inputParam} )"),
    LEFT_OPEN_INTERVAL("07", "左半开区间", "({fieldName} > {start_inputParam} and {fieldName} <= {end_inputParam} )"),
    RIGHT_OPEN_INTERVAL("08", "右半开区间", "({fieldName} >= {start_inputParam} and {fieldName} < {end_inputParam} )");

    /**
     * 编码
     */
    private String code;

    private String detail;

    private String expression;

    OperateModelEnum(String code, String detail, String expression) {
        this.code = code;
        this.detail = detail;
        this.expression = expression;
    }

    public static String getExpressionValue(String code) {
        if (StringUtils.isBlank(code)) {
            return OperateModelEnum.EXACT_MATCH.expression;
        }
        for (OperateModelEnum operateModelEnum : values()) {
            if (operateModelEnum.getCode().equals(code)) {
                return operateModelEnum.getExpression();
            }
        }
        return OperateModelEnum.EXACT_MATCH.expression;
    }

    public static boolean isBetweenOperate(String code) {
        return StringUtils.equals(OPEN_INTERVAL.getCode(), code)
                || StringUtils.equals(CLOSED_INTERVAL.getCode(), code)
                || StringUtils.equals(LEFT_OPEN_INTERVAL.getCode(), code)
                || StringUtils.equals(RIGHT_OPEN_INTERVAL.getCode(), code);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }
}
