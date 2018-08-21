package com.perfectchina.bns.controller;

import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.perfectchina.bns.model.InterfaceAccountInfo;
import com.perfectchina.bns.model.InterfaceInfoStatus;
import com.perfectchina.bns.service.InterfaceAccountService;
import com.perfectchina.bns.service.TreeNodeService;
import com.perfectchina.bns.util.CustomErrorType;


/**
 * This controller receive request and create interfaceAccount for import into the system.
 * @author Steve
 *
 */
@RestController
@RequestMapping("/api")
public class RestApiInterfaceAccountController {

	public static final Logger logger = LoggerFactory.getLogger(RestApiInterfaceAccountController.class);

	@Autowired
	InterfaceAccountService interfaceAccountService; //Service which will do all data retrieval/manipulation work

	@Autowired
	@Qualifier("simpleTreeNodeServiceImpl")
	TreeNodeService simpleNetTreeNodeService; //Service which will do all data retrieval/manipulation work

	/**
	 * upload accountsJson file then save in db interfaceAccount
	 * @param file accountsJson
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/interfaceAccount/upload") 
	public ResponseEntity<?> singleFileUpload(@RequestParam MultipartFile file) throws Exception {
		if (!file.isEmpty()) {
	        ObjectMapper mapper = new ObjectMapper();
	        JavaType javaType = mapper.getTypeFactory().constructParametricType(ArrayList.class, InterfaceAccountInfo.class);
	        JsonNode json = mapper.readTree(new InputStreamReader(file.getInputStream()));
	        String jsonStr = json.toString();
	        List<InterfaceAccountInfo> interfaceAccounts =  (List<InterfaceAccountInfo>)mapper.readValue(jsonStr, javaType); 
	        List<String> dupAccountNum = new ArrayList<String>();
	        List<InterfaceAccountInfo> noDupAccounts = new ArrayList<>();
			for ( InterfaceAccountInfo interfaceAccount : interfaceAccounts ){
				if ( simpleNetTreeNodeService.isNodeDataExist(interfaceAccount.getAccountNum() )) {
					dupAccountNum.add( interfaceAccount.getAccountNum() );
				} else {
					// set the status of the account
					interfaceAccount.setStatus( InterfaceInfoStatus.PENDING );
					noDupAccounts.add(interfaceAccount);
				}
			}
			//根据原始树，去掉重复的节点
			if(noDupAccounts.size()>0){
				interfaceAccountService.storeInterfaceAccountInfo(noDupAccounts);
				return new ResponseEntity<>(HttpStatus.OK);
			}
			if (dupAccountNum.size() > 0 ) {
				logger.error("Unable to create. A InterfaceAccountInfo with interfaceAccounts {} already exist", 
						dupAccountNum.toString() );
				
				return new ResponseEntity(new CustomErrorType("Unable to create. A InterfaceAccountInfo with account numbers " + 
						dupAccountNum.toString() + " already exist."),HttpStatus.CONFLICT);
			}
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * 列出在interfaceAccount中状态为pending的account
	 * @return
	 */
	// -------------------Retrieve All InterfaceAccountInfos---------------------------------------------
	@RequestMapping(value = "/interfaceAccount/listPendingAccounts", method = RequestMethod.GET)
	public ResponseEntity<List<InterfaceAccountInfo>> listAllInterfaceAccountInfos() {
		List<InterfaceAccountInfo> interfaceAccounts = interfaceAccountService.retrievePendingInterfaceAccountInfo();
		if (interfaceAccounts.isEmpty()) {
			return new ResponseEntity(HttpStatus.NO_CONTENT);
			// You many decide to return HttpStatus.NOT_FOUND
		}
		
		return new ResponseEntity<List<InterfaceAccountInfo>>(interfaceAccounts, HttpStatus.OK);
	}

	// -------------------Retrieve Single InterfaceAccountInfo------------------------------------------
	@RequestMapping(value = "/interfaceAccount/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getInterfaceAccountInfo(@PathVariable("id") long id) {
		logger.info("Fetching InterfaceAccountInfo with id {}", id);
		InterfaceAccountInfo interfaceAccount = interfaceAccountService.findById(id);		
		if (interfaceAccount == null) {
			logger.error("InterfaceAccountInfo with id {} not found.", id);
			return new ResponseEntity(new CustomErrorType("InterfaceAccountInfo with id " + id 
					+ " not found"), HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<InterfaceAccountInfo>(interfaceAccount, HttpStatus.OK);
	}
	
	// -------------------Create a InterfaceAccountInfo-------------------------------------------

	@RequestMapping(value = "/interfaceAccount/", method = RequestMethod.POST)
	public ResponseEntity<?> createInterfaceAccountInfo(@RequestBody InterfaceAccountInfo interfaceAccount, UriComponentsBuilder ucBuilder) {
		logger.info("Creating InterfaceAccountInfo : {}", interfaceAccount);

		if ( simpleNetTreeNodeService.isNodeDataExist(interfaceAccount.getAccountNum() )) {
			logger.error("Unable to create. A InterfaceAccountInfo with interfaceAccount {} already exist", 
					interfaceAccount.getAccountNum());
			return new ResponseEntity(new CustomErrorType("Unable to create. A InterfaceAccountInfo with name " + 			
			interfaceAccount.getAccountNum() + " already exist."),HttpStatus.CONFLICT);
		}
		interfaceAccount.setId(null);
		interfaceAccount.setStatus( InterfaceInfoStatus.PENDING );
		interfaceAccountService.saveInterfaceAccountInfo(interfaceAccount);

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/api/interfaceAccount/{id}").buildAndExpand(interfaceAccount.getId()).toUri());
		return new ResponseEntity<String>(headers, HttpStatus.CREATED);
	}

	// ------------------- Update a InterfaceAccountInfo ------------------------------------------------

	@RequestMapping(value = "/interfaceAccount/{id}", method = RequestMethod.PUT)
	public ResponseEntity<?> updateInterfaceAccountInfo(@PathVariable("id") long id, @RequestBody InterfaceAccountInfo interfaceAccount) {
		logger.info("Updating InterfaceAccountInfo with id {}", id);

		InterfaceAccountInfo currentInterfaceAccountInfo = interfaceAccountService.findById(id);

		if (currentInterfaceAccountInfo == null) {
			logger.error("Unable to update. InterfaceAccountInfo with id {} not found.", id);
			return new ResponseEntity(new CustomErrorType("Unable to upate. InterfaceAccountInfo with id " + id + " not found."),
					HttpStatus.NOT_FOUND);
		}

		logger.debug("updateInterfaceAccountInfo with id "+ id +" to "+ interfaceAccount);
		currentInterfaceAccountInfo.setAccountNum( interfaceAccount.getAccountNum() );
		currentInterfaceAccountInfo.setUplinkAccount( interfaceAccount.getUplinkAccount() );
		currentInterfaceAccountInfo.setAccountName( interfaceAccount.getAccountName() );

		interfaceAccountService.updateInterfaceAccountInfo(currentInterfaceAccountInfo);
		return new ResponseEntity<InterfaceAccountInfo>(currentInterfaceAccountInfo, HttpStatus.OK);
	}

	// ------------------- Delete a InterfaceAccountInfo-----------------------------------------

	@RequestMapping(value = "/interfaceAccount/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteInterfaceAccountInfo(@PathVariable("id") long id) {
		logger.info("Fetching & Deleting InterfaceAccountInfo with id {}", id);

		InterfaceAccountInfo interfaceAccount = interfaceAccountService.findById(id);
		if (interfaceAccount == null) {
			logger.error("Unable to delete. InterfaceAccountInfo with id {} not found.", id);
			return new ResponseEntity(new CustomErrorType("Unable to delete. InterfaceAccountInfo with id " + id + " not found."),
					HttpStatus.NOT_FOUND);
		}
		interfaceAccountService.deleteInterfaceAccountInfoById(id);
		return new ResponseEntity<InterfaceAccountInfo>(HttpStatus.NO_CONTENT);
	}

	// ------------------- Delete All InterfaceAccountInfos-----------------------------

	@RequestMapping(value = "/interfaceAccount/", method = RequestMethod.DELETE)
	public ResponseEntity<InterfaceAccountInfo> deleteAllInterfaceAccountInfos() {
		logger.info("Deleting All InterfaceAccountInfos");

		interfaceAccountService.deleteAllInterfaceAccountInfos();
		return new ResponseEntity<InterfaceAccountInfo>(HttpStatus.NO_CONTENT);
	}

	// -------------------Create batch InterfaceAccountInfo-------------------------------------------
	@RequestMapping(value = "/interfaceAccount/createBatch/", method = RequestMethod.POST)
	public ResponseEntity<?> createBatchInterfaceAccountInfo(@RequestBody List<InterfaceAccountInfo> interfaceAccounts, UriComponentsBuilder ucBuilder) {
		logger.info("Creating batch InterfaceAccountInfo : {}", interfaceAccounts);

		List<String> dupAccountNum = new ArrayList<String>();
		
		for ( InterfaceAccountInfo interfaceAccount : interfaceAccounts ){
			if ( simpleNetTreeNodeService.isNodeDataExist(interfaceAccount.getAccountNum() )) {
				dupAccountNum.add( interfaceAccount.getAccountNum() );
			} else {
				// set the status of the account
				interfaceAccount.setStatus( InterfaceInfoStatus.PENDING );				
			}
		}
		
		if (dupAccountNum.size() > 0 ) {
			logger.error("Unable to create. A InterfaceAccountInfo with interfaceAccounts {} already exist", 
					dupAccountNum.toString() );
			
			return new ResponseEntity(new CustomErrorType("Unable to create. A InterfaceAccountInfo with account numbers " + 
					dupAccountNum.toString() + " already exist."),HttpStatus.CONFLICT);
		}
		
		// No duplicate, can save
		interfaceAccountService.storeInterfaceAccountInfo(interfaceAccounts);

		HttpHeaders headers = new HttpHeaders();
		// headers.setLocation( ucBuilder.path("/api/interfaceAccount/listPendingAccounts"). );
		try {
			headers.setLocation( new URI( "/api/interfaceAccount/listPendingAccounts" ) );
		} catch (URISyntaxException e) {
			logger.error( e.toString(), e);
		}
		return new ResponseEntity<String>(headers, HttpStatus.CREATED);
	}
	
	/**
	 * 保存并添加到account中并添加到原始树
	 * confirm those newly added interface accounts and import to core bonus system
	 * and convert InterfaceAccountInfo To SimpleNetTreeNode
	 * @param ucBuilder
	 * @return
	 */
	@RequestMapping(value = "/interfaceAccount/confirmBatch/", method = RequestMethod.POST)
	public ResponseEntity<?> confirmBatchInterfaceAccountInfo(UriComponentsBuilder ucBuilder) {
		logger.info("Confirm batch InterfaceAccountInfo : ");
		//修改interfaceAccount的状态
		interfaceAccountService.confirmInterfaceAccountInfo();
		//添加到原始树
		interfaceAccountService.convertInterfaceAccountInfoToSimpleNetTreeNode();
		return new ResponseEntity(HttpStatus.NO_CONTENT);
		
	}	
	
	
	/* Sample codes for reference
	@Path("{c}")
	@GET
	@Produces("application/xml")
	public String convertCtoFfromInput(@PathParam("c") Double c) {
		Double fahrenheit;
		Double celsius = c;
		fahrenheit = ((celsius * 9) / 5) + 32;
 
		String result = "@Produces(\"application/xml\") Output: \n\nC to F Converter Output: \n\n" + fahrenheit;
		return "<ctofservice>" + "<celsius>" + celsius + "</celsius>" + "<ctofoutput>" + result + "</ctofoutput>" + "</ctofservice>";
	}	
	
 	@Path("{f}")
	@GET
	@Produces("application/json")
	public Response convertFtoCfromInput(@PathParam("f") float f) throws JSONException {
 
		JSONObject jsonObject = new JSONObject();
		float celsius;
		celsius =  (f - 32)*5/9; 
		jsonObject.put("F Value", f); 
		jsonObject.put("C Value", celsius);
 
		String result = "@Produces(\"application/json\") Output: \n\nF to C Converter Output: \n\n" + jsonObject;
		return Response.status(200).entity(result).build();
	}	
	
	
	*/
}