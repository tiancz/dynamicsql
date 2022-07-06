package com.tian.modules.data.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tian.common.adapter.context.CreateDmDataSourceContext;
import com.tian.common.enums.DatabaseTypeEnum;
import com.tian.modules.data.model.entity.ColumnInfo;
import com.tian.modules.data.service.IDataService;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.util.TablesNamesFinder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DataServiceImpl implements IDataService {

    @Autowired
    private CreateDmDataSourceContext createDmDataSourceContext;

    @Override
    public Object getColumnInfoByServiceIdAndSql(String sql, String code) throws Exception {
        Statement stmt;
        List<String> list = new ArrayList<>();
        List<ColumnInfo> finalResult = new ArrayList<ColumnInfo>();
        try {
            if (createDmDataSourceContext.verifySql(DatabaseTypeEnum.MYSQL.getContext(), sql)) {
                stmt = CCJSqlParserUtil.parse(sql);
                if (stmt instanceof Select) {
                    Select select = (Select) stmt;
                    SelectBody selectBody = select.getSelectBody();
                    if (selectBody instanceof PlainSelect) {
                        List<SelectItem> selectItems = ((PlainSelect) selectBody).getSelectItems();
                        for (SelectItem item : selectItems) {
                            String itemName = item.toString();
                            list.add(itemName);
                        }
                    }
                    TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
                    List<String> result = tablesNamesFinder.getTableList(select);
                    if (result != null && result.size() > 0) {
                        // tableName = result.get(0);
                        getScriptColumnByDsIdAndTableName(finalResult, result, list, code);
                    }
                } else {
                    throw new Exception("sql语句不正确");
                }
            }
        } catch (JSQLParserException e) {
            throw new Exception("sql语句不正确");
        }
        //按照name去重
        return finalResult.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(ColumnInfo::getName))), ArrayList::new));

    }

    /**
     * 处理脚本模式查询列信息
     *
     * @param finalResult
     * @param tableNameList
     * @param selectColumnList
     * @param code
     */
    private void getScriptColumnByDsIdAndTableName(List<ColumnInfo> finalResult, List<String> tableNameList, List<String> selectColumnList, String code) {
        tableNameList.forEach(selectTableName -> {
            String tableName = "";
            String userName = "";
            if(selectTableName.contains(".")) {
                int index = selectTableName.lastIndexOf(".");
                tableName = selectTableName.substring(index+1, selectTableName.length());
                userName = selectTableName.substring(0, index);
            }else {
                tableName = selectTableName;
            }
//            ServerResponse result = metadataService.queryColumnByDsIdAndTableName(code, tableName);
//            if (result.getErrcode() != 0) {
//                throw new ApiException(result.getErrcode(), result.getErrmsg());
//            }
//            Map data = (Map) result.getData();
            Map data = new HashMap();
            Map table = (Map)data.get("table");
            String dsUserName = (String)table.get("username");
            if(StringUtils.isNotBlank(userName)&&StringUtils.isNotBlank(dsUserName)) {
                if(!userName.equals(dsUserName)) {
                    try {
                        throw new Exception("sql语句不正确");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            List<ColumnInfo> resColumnPARMList = JSONObject.parseArray(JSON.toJSONString(data.get("columns")), ColumnInfo.class);
            if (resColumnPARMList.size() > 0) {
                if (selectColumnList.size() == 1 && selectColumnList.get(0).contains("*")) {
                    finalResult.addAll(resColumnPARMList);
                } else {
                    for (String aSelectColumnList : selectColumnList) {
                        for (ColumnInfo columnInfo : resColumnPARMList) {
                            String columnName = aSelectColumnList;
                            if (StringUtils.contains(columnName, ".")) {
                                columnName = StringUtils.split(columnName, ".")[1];
                            }
                            if (columnInfo.getName().equalsIgnoreCase(columnName)) {
                                finalResult.add(columnInfo);
                            }
                        }
                    }
                }
            }

        });
    }

    private String [] createTable(){

        Dialect dialect = null;
        if (DictEnum.MODEL_DATASOURCE_TYPE_TBDS.getCode().equals(tableDTO.getDbType())) {
            //已有其他实现
            dialect = Dialect.TBDS;
        } else if (DictEnum.MODEL_DATASOURCE_TYPE_MYSQL.getCode().equals(tableDTO.getDbType())) {
            dialect = Dialect.MySQL;
        } else if (DictEnum.MODEL_DATASOURCE_TYPE_TBASE.getCode().equals(tableDTO.getDbType())) {
            dialect = Dialect.PostgreSQL;
        } else if (DictEnum.MODEL_DATASOURCE_TYPE_ORACLE.getCode().equals(tableDTO.getDbType())) {
            dialect = Dialect.Oracle;
        } else if (DictEnum.MODEL_DATASOURCE_TYPE_POSTGREPSQL.getCode().equals(tableDTO.getDbType())) {
            dialect = Dialect.PostgreSQL;
        }

        TableModel tableModel = new TableModel();
        List<Column> columns = new ArrayList();
        List<ColumnDTO> columnDtoList = tableDTO.getColumns();

        //设置字段信息
        if (columnDtoList != null) {
            for (ColumnDTO cdto : columnDtoList) {
                Column column = new Column();
                column.setColumnName(cdto.getFieldName());
                column.setComment(cdto.getFieldComment());
                if (StringUtils.isNotEmpty(cdto.getNullable()) && "0".equals(cdto.getNullable())) {
                    column.setNullable(false);
                }
                if (StringUtils.isNotEmpty(cdto.getDefaultValue())) {
                    column.setDefaultValue(SqlGetDefaultValue.getValue(tableDTO.getDbType(), cdto.getDefaultValue()));
                }
                //转换字段类型
                column.setColumnType(JdbcUtil.translaFeildType(tableDTO.getDbType(), cdto.getFieldType()));

                if (StringUtils.isNotEmpty(cdto.getFieldLength())) {
                    Integer[] x = new Integer[2];
                    x[0] = Integer.valueOf(cdto.getFieldLength());
                    if (cdto.getScale() != null) {
                        x[1] = cdto.getScale();
                    } else {
                        x[1] = 0;
                    }
                    column.setLengths(x);
                }
                //若mysql的datatime没有设置长度，则默认设置长度为0
                boolean bf = Dialect.MySQL == dialect && "datetime".contains(cdto.getFieldType().toLowerCase());
                if (bf) {
                    column.setLengths(new Integer[]{0, 0});
                }

                if ("1".equals(cdto.getPkey())) {
                    column.setPkey(true);
                }
                columns.add(column);
            }
        }
        //设置表信息
        tableModel.setTableName(tableDTO.getTableName());
        tableModel.setColumns(columns);
        tableModel.setComment(tableDTO.getComment());

        if (dialect == null) {
            return null;
        }
        String[] sql = DDLCreateUtils.toCreateDDL(dialect, tableModel);
        //String sql2 = SqlUtils.formatSql(sql[0], tableDTO.getDbType());
        return sql;
    }

}
