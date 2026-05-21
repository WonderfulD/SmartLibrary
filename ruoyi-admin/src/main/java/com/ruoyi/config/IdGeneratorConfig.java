package com.ruoyi.config;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Configuration;

import com.github.yitter.contract.IdGeneratorOptions;
import com.github.yitter.idgen.YitIdHelper;

@Configuration
public class IdGeneratorConfig  {

    @PostConstruct
    public void init() {
        // 创建 IdGeneratorOptions 对象
        IdGeneratorOptions options = new IdGeneratorOptions((short)1);
        //设置Unix时间戳为 2024-01-01 00:00:00
        options.BaseTime = 1704038400000L;
        // 保存参数，初始化 ID 生成器
        YitIdHelper.setIdGenerator(options);
    }

}
