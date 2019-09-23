package com.zyc.pandora.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author : zhangyuchen
 * @date : 2019/9/23 17:21
 */
@Configuration
public class resource  extends WebMvcConfigurerAdapter {

    @Value("${file.basePath}")
    private String basePath;


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(basePath+"**").addResourceLocations("file:///"+basePath);
        super.addResourceHandlers(registry);
    }
}
