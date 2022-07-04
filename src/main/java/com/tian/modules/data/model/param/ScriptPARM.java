package com.tian.modules.data.model.param;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ScriptPARM {

    /**
     * 数据源id
     */
    @NotBlank(message = "数据源id不能为空")
    private String id;

    /**
     * sql语句
     */
    @NotBlank(message = "sql语句不能为空")
    private String sql;

}
