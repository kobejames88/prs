package com.perfectchina.bns.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class RestApiAllStartController {
    public static final Logger logger = LoggerFactory.getLogger(RestApiQualifiedFiveStarNetTreeNodeController.class);

    @Autowired
    private RestApiQualifiedFiveStarNetTreeNodeController restApiQualifiedFiveStarNetTreeNodeController;

    @RequestMapping(value = "/allstart", method = RequestMethod.GET)
    public ResponseEntity<?> listAccounts() {

        restApiQualifiedFiveStarNetTreeNodeController.updatePassUpGpvNet();

        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }



}
