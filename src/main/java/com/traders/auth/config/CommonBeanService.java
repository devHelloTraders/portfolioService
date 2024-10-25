package com.traders.auth.config;

import com.traders.common.properties.ConfigProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class CommonBeanService {

    @Bean
    public ConfigProperties getConfigProperties(){
        return new ConfigProperties();
    }

    @Bean
    public PasswordEncoder getPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }


}
