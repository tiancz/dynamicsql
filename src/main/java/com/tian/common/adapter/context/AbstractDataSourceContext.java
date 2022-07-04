package com.tian.common.adapter.context;

import com.tian.common.adapter.service.IDataSourceService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.Optional;

@Data
public class AbstractDataSourceContext {

    @Autowired
    Map<String, IDataSourceService> map;

    IDataSourceService getDataSourceService(String type) throws Exception {
        return Optional.ofNullable(getMap().get(type)).orElseThrow(() -> new Exception("类型：" + type + "未定义"));
    }

}
