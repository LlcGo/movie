package com.lc.project.config;

import com.lc.project.constant.CommonConstant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MyPicConfig implements WebMvcConfigurer {

    @Value(value = "${llg.path.upload}")
    private String basepath;
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/img/**").addResourceLocations("file:"+ CommonConstant.UPLOADS_IMG_PATH);
        registry.addResourceHandler("/videoSystem/file/video/**").addResourceLocations("file:"+ basepath +"/video/");
    }
}
