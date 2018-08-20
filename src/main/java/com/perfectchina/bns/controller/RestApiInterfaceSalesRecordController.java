package com.perfectchina.bns.controller;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.perfectchina.bns.model.InterfaceInfoStatus;
import com.perfectchina.bns.model.InterfaceSalesRecordInfo;
import com.perfectchina.bns.service.InterfaceSalesRecordService;

/**
 * @author: lightway
 * @createDate: 2018-7-10
 * @describe: This controller receive request and create interfaceSalesRecord
 *            for import into the system.
 */
@RestController
@RequestMapping("/api")
public class RestApiInterfaceSalesRecordController {

	public static final Logger logger = LoggerFactory.getLogger(RestApiInterfaceSalesRecordController.class);

	@Autowired
	InterfaceSalesRecordService interfaceSalesRecordService;

	/**
	 * 把interfaceSalesRecord.json 文件上传
	 * @param file
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/interfaceSalesRecord/upload")
	public ResponseEntity<?> singleFileUpload(@RequestParam MultipartFile file) throws Exception {
		if (!file.isEmpty()) {
			ObjectMapper mapper = new ObjectMapper();
			JavaType javaType = mapper.getTypeFactory().constructParametricType(ArrayList.class,
					InterfaceSalesRecordInfo.class);
			JsonNode json = mapper.readTree(new InputStreamReader(file.getInputStream()));
			String jsonStr = json.toString();
			List<InterfaceSalesRecordInfo> list = (List<InterfaceSalesRecordInfo>) mapper.readValue(jsonStr, javaType);
			for (InterfaceSalesRecordInfo interfaceSalesRecordInfo : list) {
				interfaceSalesRecordInfo.setRequestStatus(InterfaceInfoStatus.PENDING);
			}
			interfaceSalesRecordService.uploadSalesRecords(list);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * 列出所有状态为pending的salesRecord
	 * @return
	 */
	// -------------------Retrieve All InterfacelistPendingSalesRecordInfos---------------------------------------------
	@RequestMapping(value = "/interfaceSalesRecord/listPendingSalesRecord", method = RequestMethod.GET)
	public ResponseEntity<List<InterfaceSalesRecordInfo>> listAllInterfaceSalesRecordInfos() {
		List<InterfaceSalesRecordInfo> SalesRecords = interfaceSalesRecordService
				.retrievePendingInterfaceSalesRecordInfo();
		if (SalesRecords.isEmpty()) {
			return new ResponseEntity(HttpStatus.NO_CONTENT);
			// You many decide to return HttpStatus.NOT_FOUND
		}
		return new ResponseEntity<List<InterfaceSalesRecordInfo>>(SalesRecords, HttpStatus.OK);
	}

	/**
	 * 改变interfaceSalesRecord 的状态，并保存到salesRecord
	 * @param ucBuilder
	 * @return
	 */
	// confirm those newly added interface sales record and import to core bonus system
	@RequestMapping(value = "/interfaceSalesRecord/confirmBatch/", method = RequestMethod.POST)
	public ResponseEntity<?> confirmBatchInterfaceAccountInfo(UriComponentsBuilder ucBuilder) {
		logger.info("Confirm batch InterfaceSalesRecordtInfo : ");
		interfaceSalesRecordService.confirmInterfaceSalesRecordInfo();
		interfaceSalesRecordService.convertInterfaceSalesRecordInfoToSalesRecord();
		return new ResponseEntity(HttpStatus.NO_CONTENT);

	}

}
