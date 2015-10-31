package com.selebot.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

/**
 * Created by Dragos on 9/29/2015.
 */
@Configuration
//@PropertySources({
//    @PropertySource("conf/bot-config.properties"),
//})
@Import({DriverConfig.class})
@ComponentScan("com.selebot.pages")
public class AppConfig {

    @Bean
    public PropertyPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        PropertyPlaceholderConfigurer configurer = new PropertyPlaceholderConfigurer();
        configurer.setSearchSystemEnvironment(true);
        configurer.setIgnoreResourceNotFound(true);
        String configPath = System.getProperty("config.path");

        configurer.setLocation(new ClassPathResource("conf/bot-config.properties"));
        if(StringUtils.isNotEmpty(configPath)){
            configurer.setLocation(new FileSystemResource(configPath+"conf/bot-config.properties"));
        }
        return configurer;
    }
}
