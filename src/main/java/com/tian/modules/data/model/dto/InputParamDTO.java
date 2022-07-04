package com.tian.modules.data.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class InputParamDTO {

    /**
     * 中文名称
     */
    private String cnName;

    /**
     * 英文名称
     */
    @NotBlank(message = "英文名称不能为空")
    private String enName;

    /**
     * 默认值
     */
    private String defaultValue;

    /**
     * 描述
     */
    private String description;

    /**
     * 别名
     */
    private String aliasName;

    /**
     * 字段类型
     */
    @NotBlank(message = "字段类型不能为空")
    private String type;

    /**
     * 是否必填(0 非必填,1必填)
     */
    @NotBlank(message = "是否必填不能为空")
    private String required;

    /**
     * 是否必填中文(0 非必填,1必填)
     */
    private String requiredDetail;

    /**
     * 是否变更操作
     */
    private String isChange;

    /**
     * 运算逻辑
     */
    private String operator;

    /**
     * 运算逻辑中文
     */
    private String operatorDetail;
}
