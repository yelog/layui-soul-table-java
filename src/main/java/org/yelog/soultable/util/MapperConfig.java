package org.yelog.soultable.util;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan({"org.yelog.soultable.mapper"})
public class MapperConfig {


    @Bean
    SoulTableInterceptor myInterceptor() {
        return new SoulTableInterceptor();
    }

}
