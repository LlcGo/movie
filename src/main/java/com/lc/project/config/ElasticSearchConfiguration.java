package com.lc.project.config;

import org.springframework.stereotype.Component;

@Component(value = "esConfig")
public class ElasticSearchConfiguration {

    public static String apiCallIndexNamePrefix;

    public String getApiCallIndexName() {
        return apiCallIndexNamePrefix;
    }
}
