package com.tian.modules.data.controller;

import com.tian.modules.data.model.param.ScriptPARM;
import com.tian.modules.data.service.IDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.function.ServerResponse;

/**
 * 数据服务基础表相关接口
 */
@RestController
@RequestMapping(value = "/dataservice", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
@Slf4j
public class DataServiceRestController {

    @Autowired
    private IDataService dataService;

    /**
     * 根据表数据源code获取数据表
     *
     * 数据服务管理/脚本模式根据sql表获取表字段参数
     * @param scriptPARM
     * @return
     */
    @PostMapping("/script/getparameter")
    public Object getScriptParameters(@RequestBody @Validated ScriptPARM scriptPARM) throws Exception {
        return dataService.getColumnInfoByServiceIdAndSql(scriptPARM.getSql(), scriptPARM.getId());
    }

}
