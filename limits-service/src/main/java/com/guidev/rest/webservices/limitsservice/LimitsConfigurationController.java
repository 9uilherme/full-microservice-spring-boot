package com.guidev.rest.webservices.limitsservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LimitsConfigurationController {

    @Autowired
    private Environment env;

    @GetMapping("/limits")
    public LimitConfiguration test(){
        Integer maximum = Integer.parseInt(env.getProperty("limits-service.maximum"));
        Integer minimum = Integer.parseInt(env.getProperty("limits-service.minimum"));
        return new LimitConfiguration(maximum, minimum);
    }
}
