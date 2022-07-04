package com.tian.modules.data.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ReturnSetupDTO {

    /**
     * 是否分页（0不分页,1分页）
     */
    private String page;

    /**
     * 分页大小
     */
    private String pageSize;

    /**
     * 自定义过滤规则
     */
    private String customFilters;

    private List<ReturnSetupFilterDTO> returnSetupFilterDTOList;
}
