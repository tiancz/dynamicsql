package com.tian.common.adapter.service.impl;

import com.tian.common.adapter.model.entity.DataSourceInfoSql;
import com.tian.common.adapter.model.entity.SqlModel;
import com.tian.common.constant.DataPageCons;
import com.tian.common.enums.FilterConditionEnum;
import com.tian.common.enums.MysqlFieldTypeEnum;
import com.tian.common.enums.WhetherEnum;
import com.tian.modules.data.model.dto.ReturnSetupDTO;
import com.tian.modules.data.model.dto.ReturnSetupFilterDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Mysql 数据源适配
 */
@Service
public class MysqlDataSourceServiceImpl extends AbstractDataSourceService {

    private static String SEPARATOR ="`";

    public SqlModel generateSqlTable(DataSourceInfoSql dmDataSourceInfoSql) {
        StringBuilder front = new StringBuilder();//查询字段
        String behind = "";//查询条件
        StringBuilder orderBy = new StringBuilder();//排序
        StringBuilder pagination = new StringBuilder();//分页
        StringBuilder filters = new StringBuilder();//过滤条件
        StringBuilder resultSql = new StringBuilder();

        buildOutputParam(front, orderBy, dmDataSourceInfoSql,SEPARATOR);


        ReturnSetupDTO returnSetup = dmDataSourceInfoSql.getReturnSet();
        String pageSize = "";
        if (null != returnSetup) {
            pageSize = getPageSize(returnSetup);
            buildMysqlReturnSetup(returnSetup, filters,dmDataSourceInfoSql.getColumnList());
        }
        behind = buildInputParams(" 1=1 ", dmDataSourceInfoSql, filters,SEPARATOR);
        //pagination.append(" limit 'page_start?', ").append(StringUtils.isBlank(pageSize) ? DataPageCons.DEFAULT_LIMIT : pageSize).append(" ");
        pagination.append(" limit '" + DataPageCons.PAGE_START + "?', ").append(StringUtils.isBlank(pageSize) ? DataPageCons.DEFAULT_LIMIT : pageSize).append(" ");

        if (StringUtils.isNotBlank(filters)) {
            // behind = behind + " and " + filters.substring(0, filters.lastIndexOf("and"));
            behind = behind +   (filters.toString().trim().startsWith("and")?" ":" and ") + filters.substring(0, filters.lastIndexOf("and"));
        }

        resultSql.append("select ").append(front.toString()).append(" from ")
                .append(dmDataSourceInfoSql.getTableName()).append(" where ").append(behind);

        if (StringUtils.isNotBlank(orderBy)) {
            resultSql.append(" order by ").append(orderBy.substring(0, orderBy.lastIndexOf(",")));
        }
        if (StringUtils.isNotBlank(pagination)) {
            resultSql.append(" ").append(pagination);
        }
        return SqlModel.builder().sql(resultSql.toString()).table(dmDataSourceInfoSql.getTableName()).where(behind).build();
    }

    public SqlModel generateSqlScript(DataSourceInfoSql dmDataSourceInfoSql) throws Exception {
        StringBuilder front = new StringBuilder();//查询字段
        StringBuilder pagination = new StringBuilder();//分页
        StringBuilder filters = new StringBuilder();//过滤条件
        StringBuilder orderBy = new StringBuilder();//排序
        StringBuilder resultSql = new StringBuilder();
        String scriptSql = dmDataSourceInfoSql.getSql();
        if (StringUtils.isBlank(scriptSql)) {
            throw new Exception("脚本模式sql语句为空");
        }
        String tableName = " (" + scriptSql + ") st ";
        String whereCause = "";
        buildOutputParam(front, orderBy, dmDataSourceInfoSql,SEPARATOR);

        int pageSize = DataPageCons.DEFAULT_LIMIT;
        ReturnSetupDTO returnSetup = dmDataSourceInfoSql.getReturnSet();
        if (null != returnSetup) {
            if (WhetherEnum.YES.getCode().equals(returnSetup.getPage())) {
                if (StringUtils.isNotBlank(returnSetup.getPageSize()) && !StringUtils.equals("0", returnSetup.getPageSize())) {
                    pageSize = Integer.parseInt(returnSetup.getPageSize());
                }
            }
            buildMysqlReturnSetup(returnSetup, filters,dmDataSourceInfoSql.getColumnList());

        }
        String behind = buildInputParams(StringUtils.isNotBlank(whereCause) ? whereCause : " 1=1 ", dmDataSourceInfoSql, filters,SEPARATOR);

        if (StringUtils.isNotBlank(filters)) {
            behind = behind +   (filters.toString().trim().startsWith("and")?" ":" and ") + filters.substring(0, filters.lastIndexOf("and"));
        }
        //pagination.append(" limit 'page_start?', ").append(pageSize).append(" ");
        pagination.append(" limit '" + DataPageCons.PAGE_START + "?', ").append(pageSize).append(" ");

        resultSql.append("select ").append(front.toString()).append(" from ")
                .append(tableName).append(" where ").append(behind);
        if (StringUtils.isNotBlank(orderBy)) {
            resultSql.append(" order by ").append(orderBy.substring(0, orderBy.lastIndexOf(",")));
        }
        if (StringUtils.isNotBlank(pagination)) {
            resultSql.append(" ").append(pagination);
        }
        return SqlModel.builder().sql(resultSql.toString()).table(scriptSql).where(behind).build();
    }

    public String getExpressionByType(String type, String filedName) {
        String expression = MysqlFieldTypeEnum.getExpressionValue(type);
        if (StringUtils.isNotBlank(type) && StringUtils.isNotBlank(expression)) {
            return expression.replace("?", filedName + "?");
        } else {
            return "'" + filedName + "?' ";
        }
    }

    /**
     * 拼接过滤条件
     *
     * @param returnSetup
     * @param filters
     */
    public void buildMysqlReturnSetup(ReturnSetupDTO returnSetup, StringBuilder filters, List<Map> columnList) {
        if(StringUtils.isNotBlank(returnSetup.getCustomFilters())){
            if(!returnSetup.getCustomFilters().trim().startsWith("and")){
                filters.append(" and ");
            }
            filters.append(returnSetup.getCustomFilters());
            if(!returnSetup.getCustomFilters().trim().endsWith("and")){
                filters.append(" and ");
            }
            return;
        }

        if (null != returnSetup.getReturnSetupFilterDTOList() && returnSetup.getReturnSetupFilterDTOList().size() > 0) {
            for (int i = 0, len = returnSetup.getReturnSetupFilterDTOList().size(); i < len; i++) {
                ReturnSetupFilterDTO setupFilter = returnSetup.getReturnSetupFilterDTOList().get(i);
                String type=getFieldType(columnList ,setupFilter.getName());
                if (FilterConditionEnum.CONTAIN.getCode().equals(setupFilter.getOperator())) {
                    filters.append(addFieldEscape(setupFilter.getName(),SEPARATOR))
                            .append(" like ").append("'").append(setupFilter.getName()).append("_DATA_SERVICE_Q?' ").append(" and ");
                } else if (FilterConditionEnum.NOT_CONTAIN.getCode().equals(setupFilter.getOperator())) {
                    filters.append(addFieldEscape(setupFilter.getName(),SEPARATOR))
                            .append(" not like ").append("'").append(setupFilter.getName()).append("_DATA_SERVICE_Q?' ").append(" and ");
                } else {
                    if("TIME".equals(type.toUpperCase())){
                        type="TIME_RETURN";
                    }
                    filters.append(addFieldEscape(setupFilter.getName(),SEPARATOR)).append(setupFilter.getOperatorDetail()).append(" ")
                            .append(getExpressionByType(type,setupFilter.getName()+"_DATA_SERVICE_Q")).append(" and ");
                    //.append(" '").append(setupFilter.getName()).append("?' ").append(" and ");
                }

            }
        }
    }

}

