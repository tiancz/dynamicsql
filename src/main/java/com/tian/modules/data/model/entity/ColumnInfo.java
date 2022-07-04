package com.tian.modules.data.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ColumnInfo {
    private String name;

    private String comment;

    private String type;

    private String code;
}
