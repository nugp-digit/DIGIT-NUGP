package digit.web.controllers;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.response.ResponseInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import digit.service.CommonService;
import digit.service.UserService;
import digit.util.ResponseInfoFactory;

import digit.web.models.common.CommonRequest;
import digit.web.models.common.CommonResponse;
import digit.web.models.scheme.UserBankDetails;
import digit.web.models.user.InputTest;
import digit.web.models.user.UserDetails;
import digit.web.models.user.UserRequest;
import digit.web.models.user.UserResponse;
import io.swagger.annotations.ApiParam;
import jakarta.validation.Valid;

@Controller
public class UserApiController {


    @Autowired
    private UserService userService;
    @Autowired
    private ResponseInfoFactory responseInfoFactory;

     @PostMapping("/user/_get")
//    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<UserResponse>registrationSearchPost(
            @ApiParam(value = "Details for Users", required = true) 
            @Valid @RequestBody UserRequest userRequest) {
        
            UserResponse res = null;
            List<UserDetails> users = userService.getUserDetails(userRequest.getRequestInfo(), userRequest.getUserSearchCriteria());
            ResponseInfo responseInfo = responseInfoFactory
            .createResponseInfoFromRequestInfo(userRequest.getRequestInfo(), true);
             res = UserResponse.builder()
            .userDetails(users).responseInfo(responseInfo).build();  
        
        return new ResponseEntity<>(res, HttpStatus.OK);
 
    }

    @PostMapping("/user/_save")
//    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<String> saveUserDetails(@RequestBody InputTest userRequest) {
        System.out.println(userRequest);
        try {
            userService.saveUserDetails(userRequest);
            return new ResponseEntity<>("User details saved successfully.", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to save user details: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}