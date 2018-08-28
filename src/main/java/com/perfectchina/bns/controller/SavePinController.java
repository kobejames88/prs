package com.perfectchina.bns.controller;

import com.perfectchina.bns.service.SavePinService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


/**
 * This controller receive request and create network with PPV, OPV information.
 * @author Terry
 *
 */
@RestController
@RequestMapping("/api")
public class SavePinController {

	public static final Logger logger = LoggerFactory.getLogger(SavePinController.class);


	@Autowired
	SavePinService savePinService;


	@RequestMapping(value = "/pin/{snapshotDate}", method = RequestMethod.PUT)
	public ResponseEntity save(@PathVariable("snapshotDate") String snapshotDate) {
		savePinService.save(snapshotDate);
        return new ResponseEntity<>(HttpStatus.OK);
	}

}