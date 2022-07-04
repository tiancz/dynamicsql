package com.tian.common.adapter.service.impl;

import com.tian.common.adapter.model.entity.DataSourceInfoSql;
import com.tian.common.adapter.model.entity.SqlModel;
import com.tian.common.adapter.service.IDataSourceService;
import com.tian.common.constant.DataSourceCons;
import com.tian.common.enums.FilterConditionEnum;
import com.tian.common.enums.ModelTypeEnum;
import com.tian.common.enums.OperateModelEnum;
import com.tian.common.enums.OrderByEnum;
import com.tian.common.enums.WhetherEnum;
import com.tian.modules.data.model.dto.InputParamDTO;
import com.tian.modules.data.model.dto.OutputParamDTO;
import com.tian.modules.data.model.dto.ReturnSetupDTO;
import com.tian.modules.data.model.dto.ReturnSetupFilterDTO;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.util.TablesNamesFinder;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

@Slf4j
public abstract class AbstractDataSourceService implements IDataSourceService {

    @Override
    public boolean verificationSqlBySelect(String sql) {
//        String tmp = sql.trim().substring(0,6).toUpperCase();
//        if(tmp.startsWith("INSERT") || tmp.startsWith("DELETE") || tmp.startsWith("UPDATE") ||
//                tmp.startsWith("UPDATE") || tmp.startsWith("DROP") || tmp.startsWith("TRUNCA") ||
//                tmp.startsWith("REPLAC")) {
//            return false;
//        }
        Statement stmt;
        try {
            sql = sql.trim().toUpperCase();
            if (sql.startsWith("SELECT") && sql.contains(" INTO ")) {
                throw new Exception("sql语句不正确");
            }
            stmt = CCJSqlParserUtil.parse(sql);
//            if (stmt instanceof Select) {
//                return true;
//            }
            if (stmt instanceof Select) {
                Select select = (Select) stmt;
                SelectBody selectBody = select.getSelectBody();
                if (selectBody instanceof PlainSelect) {
                    List<SelectItem> selectItems = ((PlainSelect) selectBody).getSelectItems();
                    if (null == selectItems || selectItems.size() < 1) {
                        throw new Exception("SQL_ERROR");
                    }
                }
                TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
                List<String> result = tablesNamesFinder.getTableList(select);
                if (null == result.get(0)) {
                    throw new Exception("SQL_ERROR");
                }
                return true;
            } else {
                throw new Exception("SQL_ERROR");
            }
        } catch (Exception e) {
            log.error("sql语句不正确: ",e);
            return false;
        }
    }

    @Override
    public SqlModel generateSql(DataSourceInfoSql dmDataSourceInfoSql) throws Exception {
        if (ModelTypeEnum.SCRIPT.getCode().equals(dmDataSourceInfoSql.getModeType())) {
            //脚本模式
            return generateSqlScript(dmDataSourceInfoSql);
        } else if (ModelTypeEnum.TABLE.getCode().equals(dmDataSourceInfoSql.getModeType())) {
            //表单模式（向导）
            return generateSqlTable(dmDataSourceInfoSql);
        } else {
            try {
                throw new Exception("ModeType: " + dmDataSourceInfoSql.getModeType() + "  为空或未定义");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    /**
     * 获取分页
     *
     * @param returnSetup
     * @return
     */
    public String getPageSize(ReturnSetupDTO returnSetup) {
        String pageSize = "";
        if (WhetherEnum.YES.getCode().equals(returnSetup.getPage())) {
            pageSize = (StringUtils.isBlank(returnSetup.getPageSize())
                    || StringUtils.equals("0", returnSetup.getPageSize()))
                    ? "" : returnSetup.getPageSize();
        }
        return pageSize;
    }

    /**
     * 拼接过滤条件
     *
     * @param returnSetup
     * @param filters
     */
    public void buildReturnSetup(ReturnSetupDTO returnSetup, StringBuilder filters, List<Map> columnList, String separator) {
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
                String type = getFieldType(columnList, setupFilter.getName());
                if (FilterConditionEnum.CONTAIN.getCode().equals(setupFilter.getOperator())) {
                    filters.append(addFieldEscape(setupFilter.getName(),separator))
                            .append(" like ").append("'").append(setupFilter.getName()).append("_DATA_SERVICE_Q?' ").append(" and ");
                } else if (FilterConditionEnum.NOT_CONTAIN.getCode().equals(setupFilter.getOperator())) {
                    filters.append(addFieldEscape(setupFilter.getName(),separator))
                            .append(" not like ").append("'").append(setupFilter.getName()).append("_DATA_SERVICE_Q?' ").append(" and ");
                } else {
                    filters.append(addFieldEscape(setupFilter.getName(),separator)).append(setupFilter.getOperatorDetail()).append(" ")
                            .append(getExpressionByType(type, setupFilter.getName() + "_DATA_SERVICE_Q")).append(" and ");
                    //.append(" '").append(setupFilter.getName()).append("?' ").append(" and ");
                }

            }
        }
    }

    /**
     * 构造查询列
     *
     * @param front
     * @param orderBy
     * @param dmDataSourceInfoSql
     */
    public void buildOutputParam(StringBuilder front, StringBuilder orderBy, DataSourceInfoSql dmDataSourceInfoSql,String separator ) {
        if (null != dmDataSourceInfoSql.getOutputParamList() && dmDataSourceInfoSql.getOutputParamList().size() > 0) {
            for (int i = 0, len = dmDataSourceInfoSql.getOutputParamList().size(); i < len; i++) {
                OutputParamDTO outputParam = dmDataSourceInfoSql.getOutputParamList().get(i);
                if (i <= len - 1) {
                    front.append(addFieldEscape(outputParam.getEnName(),separator));
                    if (StringUtils.isNotBlank(outputParam.getSort())) {
                        orderBy.append(addFieldEscape(outputParam.getEnName(),separator)).append(' ')
                                .append(OrderByEnum.getByCode(outputParam.getSort()).getDetail()).append(",");
                    }
                }
                if (i != len - 1) {
                    front.append(",");
                }
            }
        }
    }

    /**
     * 构造查询条件
     *
     * @param whereCause
     * @param dmDataSourceInfoSql
     * @return
     */
    public String buildInputParams(String whereCause, DataSourceInfoSql dmDataSourceInfoSql, StringBuilder filters,String separator) {
        StringBuilder behind = new StringBuilder();//查询条件
        if (StringUtils.isNotBlank(whereCause)) {
            behind.append(whereCause).append(" and ");
        }
        if (null != dmDataSourceInfoSql.getInputParamList() && dmDataSourceInfoSql.getInputParamList().size() > 0) {
            for (int i = 0, len = dmDataSourceInfoSql.getInputParamList().size(); i < len; i++) {
                InputParamDTO inputParam = dmDataSourceInfoSql.getInputParamList().get(i);
                // 过滤条件中已经包含的查询列，不做拼接
                String field = "'" + inputParam.getEnName() + "?'";
                if ((null != filters && filters.indexOf(field) > -1)) {
                    continue;
                }
                String expression = OperateModelEnum.getExpressionValue(inputParam.getOperator());
                String cause = expression.replace(DataSourceCons.FIELD_NAME_VARIABLE,addFieldEscape(inputParam.getEnName(),separator))
                        .replace(DataSourceCons.INPUT_PARAM_VARIABLE,getExpressionByType(inputParam.getType(), inputParam.getEnName()))
                        .replace(DataSourceCons.START_INPUT_PARAM_VARIABLE,getExpressionByType(inputParam.getType(), DataSourceCons.START_SUFFIX+inputParam.getEnName()))
                        .replace(DataSourceCons.END_INPUT_PARAM_VARIABLE,getExpressionByType(inputParam.getType(), DataSourceCons.END_SUFFIX+inputParam.getEnName()));
                behind.append(cause);
              /*  behind.append(" = ").append("'");
                behind.append(inputParam.getEnName());
                behind.append("?' ");*/
                behind.append(" and ");
            }
        }
        if (StringUtils.isNotBlank(behind)) {
            return behind.substring(0, behind.lastIndexOf("and"));
        }
        return behind.toString();
    }

    public void buildScriptParams(DataSourceInfoSql dmDataSourceInfoSql, StringBuilder front) {
        if (null != dmDataSourceInfoSql.getOutputParamList() && dmDataSourceInfoSql.getOutputParamList().size() > 0) {
            for (int i = 0, len = dmDataSourceInfoSql.getOutputParamList().size(); i < len; i++) {
                OutputParamDTO outputParam = dmDataSourceInfoSql.getOutputParamList().get(i);
                if (i <= len - 1) {
                    front.append(outputParam.getEnName());
                }
                if (i != len - 1) {
                    front.append(",");
                }
            }
        }
    }

    public String getFieldType(List<Map> columnList, String fieldName) {
        for (Map column : columnList) {
            if (fieldName.equals(column.get("name"))) {
                return (String) column.get("code");
            }
        }
        return null;
    }

    public abstract String getExpressionByType(String type, String filedName);

    public abstract SqlModel generateSqlTable(DataSourceInfoSql dmDataSourceInfoSql);

    public abstract SqlModel generateSqlScript(DataSourceInfoSql dmDataSourceInfoSql) throws Exception;

    /**
     * 根据数据库类型加转义
     *
     * @param filedName 列名
     * @param separator 分隔符
     * @return 拼接之后的列
     */
    public String addFieldEscape(String filedName, String separator) {
        String result = "";
        String newColumnName = filedName;
        if (filedName.contains(".")) {
            result = filedName.substring(0, filedName.lastIndexOf("."));
            newColumnName = filedName.substring(filedName.lastIndexOf(".") + 1, filedName.length());
        }
        if (!(newColumnName.startsWith(separator) && newColumnName.endsWith(separator))) {
            newColumnName = separator + newColumnName + separator;
        }
        return result + newColumnName;
    }
}
