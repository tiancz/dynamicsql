package com.tian.common.enums;

public enum DatabaseTypeEnum {
    MYSQL("1", "com.mysql.cj.jdbc.Driver","mysqlDataSourceServiceImpl","mysql"),
    ORACLE("2", "oracle.jdbc.driver.OracleDriver","oracleDataSourceServiceImpl","oracle"),
    PGSQL("3", "org.postgresql.Driver","tbaseDataSourceServiceImpl","postgresql"),
    //SQLSERVER("4", "",""),
    TDATA("5", "oracle.jdbc.driver.OracleDriver","oracleDataSourceServiceImpl","tdata"),
    TBASE("6", "org.postgresql.Driver","tbaseDataSourceServiceImpl","tbase"),
    TDSQL("7", "com.mysql.cj.jdbc.Driver","mysqlDataSourceServiceImpl","tdsql");


    private String code;
    private String message;
    private String context;
    private String type;

    private DatabaseTypeEnum(String code, String message,String context,String type) {
        this.code = code;
        this.message = message;
        this.context = context;
        this.type = type;
    }

    public static DatabaseTypeEnum getByCode(String index) {
        for (DatabaseTypeEnum orgOperateIDEnum : values()) {
            if (orgOperateIDEnum.getCode().equals(index)) {
                return orgOperateIDEnum;
            }
        }
        return null;
    }

    public String getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

    public String getContext() {
        return this.context;
    }

    public String getType() {
        return type;
    }
}
