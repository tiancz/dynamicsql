package com.tian.common.adapter.service;

import com.tian.common.adapter.model.entity.DataSourceInfoSql;
import com.tian.common.adapter.model.entity.SqlModel;

public interface IDataSourceService {

    boolean verificationSqlBySelect(String sql);

    SqlModel generateSql(DataSourceInfoSql dmDataSourceInfoSql) throws Exception;
}
