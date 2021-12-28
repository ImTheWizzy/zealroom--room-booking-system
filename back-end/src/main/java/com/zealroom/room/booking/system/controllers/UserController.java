package com.zealroom.room.booking.system.controllers;

import com.zealroom.room.booking.system.entities.Organization;
import com.zealroom.room.booking.system.entities.UserOrganizationConnection;
import com.zealroom.room.booking.system.repositories.OrganizationRepository;
import com.zealroom.room.booking.system.repositories.UserOrganizationConnectionRepository;
import com.zealroom.room.booking.system.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.zealroom.room.booking.system.entities.User;
import com.zealroom.room.booking.system.exceptions.UserAuthenticationException;
import com.zealroom.room.booking.system.helpers.HelperService;

import java.util.List;

@RestController
@CrossOrigin(origins = "localhost:4200")
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserOrganizationConnectionRepository userOrganizationConnectionRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody User newUser) {
        if(!checkUser(newUser).equals("")){
            return new ResponseEntity<>(checkUser(newUser),HttpStatus.BAD_REQUEST);
        }
        if(userRepository.findByEmail(newUser.getEmail()) != null){
            return new ResponseEntity<>("Email already in use",HttpStatus.BAD_REQUEST);        }
        try{
            newUser.setPassword(encoder.encode(newUser.getPassword()));
            newUser.setIsAdmin(false);
            userRepository.save(newUser);
            return new ResponseEntity<>("Register successful",HttpStatus.OK);
        }catch(DataIntegrityViolationException e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/register/admin/{sessionToken}")
    public ResponseEntity registerAdmin(@RequestBody User newUser, @PathVariable String sessionToken) {
        User admin = userRepository.findBySessionToken(sessionToken);
        if(admin == null){
            return new ResponseEntity<>("Incorrect sessionToken",HttpStatus.BAD_REQUEST);
        }
        if(!admin.getIsAdmin()) {
            return new ResponseEntity<>("Only admins can create admin accounts",HttpStatus.BAD_REQUEST);
        }
        if(!checkUser(newUser).equals("")){
            return new ResponseEntity<>(checkUser(newUser),HttpStatus.BAD_REQUEST);
        }
        if(userRepository.findByEmail(newUser.getEmail()) != null){
            return new ResponseEntity<>("Email already in use",HttpStatus.BAD_REQUEST);
        }
        try{
            newUser.setPassword(encoder.encode(newUser.getPassword()));
            newUser.setIsAdmin(true);
            userRepository.save(newUser);
            return new ResponseEntity<>("Register successful",HttpStatus.OK);
        }catch(DataIntegrityViolationException e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }

    private String checkUser(User user){
        String message = "";
        if(user.getFirstName() == null || user.getFirstName().equals("")){
            message = "First Name must not be empty.";
        }else if(user.getLastName() == null || user.getLastName().equals("")){
            message = "Last Name must not be empty.";
        }else if(user.getEmail() == null || user.getEmail().equals("")){
            message = "Email must not be empty.";
        }else if(user.getPassword() == null || user.getPassword().equals("")){
            message = "Password must not be empty.";
        }
        return message;
    }

    @GetMapping("/{uuid}")
    public User user(@PathVariable String uuid) {
        return userRepository.findByUuid(uuid);
    }

    @PutMapping("/login")
    public User login(@RequestBody ObjectNode emailAndPasswordInJson) {
        String email = emailAndPasswordInJson.get("email").asText();
        String password = emailAndPasswordInJson.get("password").asText();
        User user;
        try{
            user = authenticateAndReturnUser(email, password);
            String newSessionToken = HelperService.generateNewToken();
            user.setSessionToken(newSessionToken);
            userRepository.save(user);
            return user;
        }catch(UserAuthenticationException e){
            return null;
        }
    }

    @PutMapping("/logout")
    public String logout(@RequestBody String sessionToken){
        User user = getUserBySessionTokenInJson(userRepository,sessionToken);
        if(user == null){
            return HelperService.toJson("error","Incorrect sessionToken");
        }
        user.setSessionToken(null);
        userRepository.save(user);
        return HelperService.toJson("success", "Logout successful.");
    }

    @GetMapping("/get")
    public User getUserBySessionToken(@RequestBody String sessionToken){
        return getUserBySessionTokenInJson(userRepository,sessionToken);
    }

    @PostMapping("/register/moderator/{sessionToken}/{organizationUuid}")
    public ResponseEntity registerModerator(@RequestBody User newUser, @PathVariable String sessionToken, @PathVariable String organizationUuid) {
        Organization organization = organizationRepository.findByUuid(organizationUuid);
        if(organization == null){
            return new ResponseEntity<>("Wrong organization uuid",HttpStatus.BAD_REQUEST);
        }
        User moderator = userRepository.findBySessionToken(sessionToken);
        if(moderator == null){
            return new ResponseEntity<>("Incorrect session token",HttpStatus.BAD_REQUEST);
        }
        if(!isModerator(moderator,organization)) {
            return new ResponseEntity<>("Only moderators can add moderator accounts",HttpStatus.BAD_REQUEST);
        }
        if(!checkUser(newUser).equals("")){
            return new ResponseEntity<>(checkUser(newUser),HttpStatus.BAD_REQUEST);
        }
        if(userRepository.findByEmail(newUser.getEmail()) != null){
            return new ResponseEntity<>("Email already in use",HttpStatus.BAD_REQUEST);
        }
        try{
            newUser.setPassword(encoder.encode(newUser.getPassword()));
            newUser.setIsAdmin(false);
            userRepository.save(newUser);
            UserOrganizationConnection userOrganizationConnection = new UserOrganizationConnection(organization,newUser,true);
            userOrganizationConnectionRepository.save(userOrganizationConnection);
            return new ResponseEntity<>("Manager creation successful",HttpStatus.OK);
        }catch(DataIntegrityViolationException e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }

    private User authenticateAndReturnUser(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user == null){
            throw new UserAuthenticationException("Wrong email or password.");
        }
        if(encoder.matches(password, user.getPassword())) {
            return user;
        }else {
            throw new UserAuthenticationException("Wrong email or password.");
        }
    }

    public static User getUserBySessionTokenInJson(UserRepository userRepository,String jsonBody) {
        String sessionToken = HelperService.valueOfARepresentingKeyInJsonString("sessionToken",jsonBody);
        return userRepository.findBySessionToken(sessionToken);
    }

    public boolean isModerator(User user, Organization organization){
       List<User> moderators = userOrganizationConnectionRepository.getOrganizationModerators(organization);

       if(moderators.contains(user)){
           return true;
       }
       return false;
    }

}