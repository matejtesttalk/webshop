package com.example.webshop.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@ConfigurationProperties(HNBApiProperties.HNB_PREFIX)
public class HNBApiProperties implements Validator {


    public static final String HNB_PREFIX = "hnb";

    private String url;

    private Integer timeOutSeconds;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return HNBApiProperties.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        HNBApiProperties properties = (HNBApiProperties) target;

        if (!StringUtils.hasLength(properties.getUrl())) {
            errors.rejectValue("url", "NotEmpty", "url should not be empty or null.");
        }
        if (timeOutSeconds <= 0) {
            errors.rejectValue("timeOutSeconds", "NotEmpty", "timeOutSeconds should not be lower or equal to 0");
        }
    }

    public Integer getTimeOutSeconds() {
        return timeOutSeconds;
    }

    public void setTimeOutSeconds(Integer timeOutSeconds) {
        this.timeOutSeconds = timeOutSeconds;
    }
}
