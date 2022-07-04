package com.tian.common.adapter.model.entity;

import com.tian.modules.data.model.dto.InputParamDTO;
import com.tian.modules.data.model.dto.OutputParamDTO;
import com.tian.modules.data.model.dto.ReturnSetupDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * SQL生成入参
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class DataSourceInfoSql {
    /**
     * 模式 脚本模式、向导模式
     */
    private String modeType;

    /**
     * 脚本模式SQL语句
     */
    private String sql;

    /**
     * 表名
     */
    private String tableName;
    /**
     * 输出参数
     */
    private List<OutputParamDTO> outputParamList;

    /**
     * 输入参数
     */
    private List<InputParamDTO> inputParamList;

    /**
     * 返回结果参数
     */
    private ReturnSetupDTO returnSet;

    private List<Map> columnList;
}
