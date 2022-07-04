package com.tian.modules.data.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ReturnSetupFilterDTO {

    /**
     * 字段名
     */
    private String name;

    /**
     * 值
     */
    private String value;

    /**
     * 操作
     */
    private String operator;

    /**
     * 操作详情
     */
    private String operatorDetail;
}
