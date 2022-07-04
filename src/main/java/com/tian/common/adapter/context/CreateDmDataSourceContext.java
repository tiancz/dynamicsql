package com.tian.common.adapter.context;

import com.tian.common.adapter.model.entity.DataSourceInfoSql;
import com.tian.common.adapter.model.entity.SqlModel;
import org.springframework.stereotype.Component;

/**
 * 策略器
 */
@Component
public class CreateDmDataSourceContext extends AbstractDataSourceContext {

    public SqlModel create(String type, DataSourceInfoSql dmDataSourceInfoSql) throws Exception {
        return  getDataSourceService(type).generateSql(dmDataSourceInfoSql);
    }

    public boolean verifySql(String type, String sql) throws Exception {
        return  getDataSourceService(type).verificationSqlBySelect(sql);
    }
}
