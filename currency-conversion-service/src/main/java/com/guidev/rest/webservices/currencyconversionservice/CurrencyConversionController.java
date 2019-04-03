package com.guidev.rest.webservices.currencyconversionservice;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.ribbon.hystrix.FallbackHandler;
import com.netflix.ribbon.proxy.annotation.Hystrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
public class CurrencyConversionController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CurrencyExchangeServiceProxy proxy;

    @GetMapping("/currency-conversion/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversionBean
    currencyConversion(@PathVariable String from,
                       @PathVariable String to,
                       @PathVariable BigDecimal quantity) {

        Map<String,String> uriVariables = new HashMap<>();
        uriVariables.put("from", from);
        uriVariables.put("to", to);
        ResponseEntity<CurrencyConversionBean> response = new RestTemplate().getForEntity(
                "http://localhost:8000/currency-exchange/from/{from}/to/{to}", CurrencyConversionBean.class, uriVariables
        );

        CurrencyConversionBean currencyConversionBean = response.getBody();

        logger.info("{}", response);

        return new CurrencyConversionBean(currencyConversionBean.getId(),
                from,
                to,
                currencyConversionBean.getConversionMultiple(),
                quantity,
                quantity.multiply(currencyConversionBean.getConversionMultiple()),
                currencyConversionBean.getPort());
    }
    @GetMapping("/currency-conversion-feign/from/{from}/to/{to}/quantity/{quantity}")
    @HystrixCommand(fallbackMethod = "fallBackConversion")
    public CurrencyConversionBean
    currencyConversionFeign(@PathVariable String from,
                       @PathVariable String to,
                       @PathVariable BigDecimal quantity) {
       // Don't need the RestTemplate anymore, now we got the Rest Service Client called Feign

        CurrencyConversionBean currencyConversionBean = proxy.retrieveExchangeValue(from,to);

//        logger.info("{}", currencyConversionBean);

        return new CurrencyConversionBean(currencyConversionBean.getId(),
                from,
                to,
                currencyConversionBean.getConversionMultiple(),
                quantity,
                quantity.multiply(currencyConversionBean.getConversionMultiple()),
                currencyConversionBean.getPort());
    }

    public CurrencyConversionBean fallBackConversion(String from, String to, BigDecimal quantity){
        CurrencyConversionBean currencyConversionBean = new CurrencyConversionBean();
        logger.error("Unavailable exchange service");
        return new CurrencyConversionBean(currencyConversionBean.getId(),
                from,
                to,
                null,
                quantity,
                null,
                0);
    }

}
