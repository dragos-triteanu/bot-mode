package com.selebot.config;

import com.selebot.webdriver.WebDriverFactory;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Dragos on 9/29/2015.
 */

@Configuration
public class DriverConfig {

    @Value("${driver.wait.for.element.seconds}")
    private int implicitlyWait;

    @Value("${driver.type}")
    private String driverType;

    @Value("${drive.page.load.seconds}")
    private int maxPageLoadTimeout;

    @Bean(name = "webDriver")
    public WebDriver webDriver(){
        WebDriverFactory driverFactory = new WebDriverFactory(implicitlyWait,maxPageLoadTimeout);
        return driverFactory.create(driverType);
    }
}
