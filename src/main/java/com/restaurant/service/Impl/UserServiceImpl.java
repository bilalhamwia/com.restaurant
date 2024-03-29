package com.restaurant.service.Impl;

import com.restaurant.JWT.CustomerUserDetailsService;
import com.restaurant.JWT.JwtFilter;
import com.restaurant.JWT.JwtUtil;
import com.restaurant.Entity.User;
import com.restaurant.constents.RestaurantConstants;
import com.restaurant.repository.UserRepository;
import com.restaurant.service.UserService;
import com.restaurant.utils.EmailUtils;
import com.restaurant.utils.RestaurantUtils;
import com.restaurant.wrapper.UserWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    CustomerUserDetailsService customerUserDetailsService;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    JwtFilter jwtFilter;

    @Autowired
    EmailUtils emailUtils;

    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
        log.info("Inside signup {}", requestMap);
        try {
            if (validateSignUpMap(requestMap)) {
                User user = userRepository.findByEmailId(requestMap.get("email"));
                if (Objects.isNull(user)) {
                    userRepository.save(getUserFromMap(requestMap));
                    return RestaurantUtils.getResponseEntity("Successfully Registered.", HttpStatus.OK);
                } else {
                    return RestaurantUtils.getResponseEntity("Email already exits.", HttpStatus.BAD_REQUEST);
                }
            } else {
                return RestaurantUtils.getResponseEntity(RestaurantConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return RestaurantUtils.getResponseEntity(RestaurantConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private boolean validateSignUpMap(Map<String, String> requestMap) {
        return requestMap.containsKey("name")
                && requestMap.containsKey("contactNumber")
                && requestMap.containsKey("email")
                && requestMap.containsKey("password");
    }

    private User getUserFromMap(Map<String, String> requestMap) {
        User user = new User();
        user.setName(requestMap.get("name"));
        user.setContactNumber(requestMap.get("contactNumber"));
        user.setEmail(requestMap.get("email"));
        user.setPassword(requestMap.get("password"));
        user.setStatus("false");
        user.setRole("user");
        return user;
    }

    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
        log.info("Inside login");
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(requestMap.get("email"), requestMap.get("password")));
            if(authentication.isAuthenticated()){
                if(customerUserDetailsService.getUserDetail().getStatus().equalsIgnoreCase("true")){
                    return new ResponseEntity<String>("{\"token\":\"" +
                            jwtUtil.generateToken(customerUserDetailsService.getUserDetail().getEmail(),customerUserDetailsService.getUserDetail().getRole()) + "\"}",HttpStatus.OK);
                } else {
                    return new ResponseEntity<String>("{\"message\":\""+"Wait for admin approval." + "\"}", HttpStatus.BAD_REQUEST);
                }
            }
        } catch (Exception exception){
            log.error("{}", exception);
        }
        return new ResponseEntity<String>("{\"message\":\""+"Bad credentials." + "\"}", HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<List<UserWrapper>> getAllUser() {
        try {
            if(jwtFilter.isAdmin())  {
                return new ResponseEntity<>(userRepository.getAllUser(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> update(Map<String, String> requestMap) {
        try {
            if(jwtFilter.isAdmin()){
                Optional<User> user = userRepository.findById(Integer.parseInt(requestMap.get("id")));
                if(user.isPresent()){
                    userRepository.updateStatus(requestMap.get("status"), Integer.parseInt(requestMap.get("id")));
                    sendMailToAllAdmin(requestMap.get("status"), user.get().getEmail(), userRepository.getAllAdmin());
                    return RestaurantUtils.getResponseEntity("User status updated successfully", HttpStatus.OK);
                } else {
                    return RestaurantUtils.getResponseEntity("User id does not exist", HttpStatus.NO_CONTENT);
                }
            } else return RestaurantUtils.getResponseEntity(RestaurantConstants.UNAUTHORIZED_ACCESS,HttpStatus.UNAUTHORIZED);
        } catch (Exception exception){
            exception.printStackTrace();
        }
        return RestaurantUtils.getResponseEntity(RestaurantConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void sendMailToAllAdmin(String status, String user, List<String> allAdmin) {
        allAdmin.remove(jwtFilter.getCurrentUser());
        if (status != null && status.equalsIgnoreCase("true")) {
            emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(), "Account Approved", "User:- "+user+"\n is approved by \nADMIN:-" + jwtFilter.getCurrentUser(), allAdmin);
        } else {
            emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(), "Account Disabled", "User:- "+user+"\n is Disabled by \nADMIN:-" + jwtFilter.getCurrentUser(), allAdmin);

        }
    }

    @Override
    public ResponseEntity<String> checkToken() {
        return RestaurantUtils.getResponseEntity("true", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> changePassword(Map<String, String> requestMap) {
        try {
            User user = userRepository.findByEmail(jwtFilter.getCurrentUser());
            if (!user.equals(null)) {
                if (user.getPassword().equals(requestMap.get("oldPassword"))) {
                    user.setPassword(requestMap.get("newPassword"));
                    userRepository.save(user);
                    return RestaurantUtils.getResponseEntity("Password Update Successfully", HttpStatus.OK);
                }
                return RestaurantUtils.getResponseEntity("Incorrect old Password", HttpStatus.BAD_REQUEST);
            }
            return RestaurantUtils.getResponseEntity(RestaurantConstants.INVALID_DATA, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return RestaurantUtils.getResponseEntity(RestaurantConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> forgotPassword(Map<String, String> requestMap) {
        try {
            User user = userRepository.findByEmail(requestMap.get("email"));
            if(!Objects.isNull(user) && !Strings.isBlank(user.getEmail()))
                emailUtils.forgotPassword(user.getEmail(), "Credentials by cafe Management System", user.getPassword());
                return RestaurantUtils.getResponseEntity("Check your mail for Credentials", HttpStatus.OK);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return RestaurantUtils.getResponseEntity(RestaurantConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
