package org.yelog.soultable;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"org.yelog.soultable.**"})
public class SoulTableApplication {
    public static void main(String[] args) {
        SpringApplication.run(SoulTableApplication.class, args);
    }
}
