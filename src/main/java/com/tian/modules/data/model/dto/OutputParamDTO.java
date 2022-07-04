package com.tian.modules.data.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class OutputParamDTO {

    private static final long serialVersionUID=1L;

    /**
     * 中文名称
     */
    private String cnName;

    /**
     * 英文名称
     */
    private String enName;

    /**
     * 别名
     */
    private String aliasName;

    /**
     * 字段类型
     */
    private String type;

    /**
     * 描述
     */
    private String description;

    /**
     * 排序
     */
    private String sort;

    /**
     * 排序中文
     */
    private String sortDetail;

    /**
     * 是否变更操作
     */
    private String isChange;
}
