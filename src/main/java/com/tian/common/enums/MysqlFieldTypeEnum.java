package com.tian.common.enums;

import org.apache.commons.lang3.StringUtils;

public enum MysqlFieldTypeEnum {
    DATETIME("DATETIME","STR_TO_DATE( '?' ,'%Y-%m-%d %H:%i:%s')"),
    TIMESTAMP("TIMESTAMP","STR_TO_DATE( '?' ,'%Y-%m-%d %H:%i:%s')"),
    DATE("DATE","STR_TO_DATE( '?' ,'%Y-%m-%d')"),
    TIME_RETURN("TIME_RETURN","DATE_FORMAT( '?' ,'%H:%i:%s')"),
    TIME("TIME","DATE_FORMAT(CONCAT('2020-05-22 ', '?' ),'%H:%i:%s')"),
    BIGINT("BIGINT","CAST( '?'  AS DECIMAL(20))"),
    INT("INT","CAST( '?'  AS SIGNED INTEGER)"),
    DECIMAL("DECIMAL","CAST( '?'  AS DECIMAL(20,15))"),
    TINYINT("TINYINT","CAST( '?'  AS SIGNED INTEGER)"),
    DOUBLE("DOUBLE","CAST( '?'  AS DECIMAL(20,15))"),
    FLOAT("FLOAT","CAST( '?'  AS DECIMAL(20,15))"),
    SMALLINT("SMALLINT","CAST( '?'  AS SIGNED INTEGER)");
    /**
     * 字段类型
     */
    private  String type;

    /**
     * 转换表达式
     */
    private  String expression;

    private MysqlFieldTypeEnum(String type, String expression){
        this.type=type;
        this.expression = expression;
    }


    public static String getExpressionValue(String type) {
        if(StringUtils.isBlank(type)){
            return null;
        }
        for (MysqlFieldTypeEnum oracleFieldTypeEnum : values()) {
            if (oracleFieldTypeEnum.getType().equals(type.toUpperCase())) {
                return oracleFieldTypeEnum.getExpression();
            }
        }
        return null;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }
}
