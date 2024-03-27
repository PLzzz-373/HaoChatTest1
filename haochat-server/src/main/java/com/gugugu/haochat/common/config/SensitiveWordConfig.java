package com.gugugu.haochat.common.config;

import com.gugugu.haochat.common.utils.sensitive.SensitiveWordBs;
import com.gugugu.haochat.common.utils.sensitive.impl.DFAFilter;
import com.gugugu.haochat.common.utils.sensitive.impl.MyWordDeny;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
public class SensitiveWordConfig {

    @Resource
    private MyWordDeny myWordDeny;

    /**
     * 初始化引导类
     *
     * @return 初始化引导类
     * @since 1.0.0
     */
    @Bean
    public SensitiveWordBs sensitiveWordBs() {
        return SensitiveWordBs.newInstance()
                .filterStrategy(DFAFilter.getInstance())
                .sensitiveWord(myWordDeny)
                .init();
    }

}
