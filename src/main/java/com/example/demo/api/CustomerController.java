package com.example.demo.api;

import com.example.demo.model.CustomerEntity;
import com.example.demo.repository.CustomerRepository;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/customer")
public class CustomerController {

    @Autowired
    CustomerRepository customerRepository;
    @ResponseBody
    @GetMapping
    public Object getCustomer(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uid = authentication.getName();
        FirebaseToken firebaseToken = (FirebaseToken) authentication.getCredentials();
        Map<String, Object> body = new HashMap<>();
        body.put("uid", uid);
        body.put("customer", customerRepository.findByUid(uid));
        body.put("isEmailVerified", firebaseToken.isEmailVerified());
        return body;
    }

    @ResponseBody
    @PostMapping
    public ResponseEntity<?> updateCustomer(@RequestBody @Valid CustomerEntity customerEntity) {
        // name = uid
        if(customerRepository.findByName(customerEntity.getName()) == null){
            String uid = SecurityContextHolder.getContext().getAuthentication().getName();
            customerEntity.setUid(uid);
            customerRepository.save(customerEntity);
            return ResponseEntity.ok(customerEntity);
        }
        Map<String, String> body = new HashMap<>();
        body.put("err", "Name has been take, please use a different name");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(body);
    }
}
