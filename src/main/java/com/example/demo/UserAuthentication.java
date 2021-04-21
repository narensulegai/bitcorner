package com.example.demo;

import com.example.demo.model.CustomerEntity;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.*;

public class UserAuthentication implements Authentication {
    String name = null;
    boolean isAuthenticated = false;
    CustomerEntity customerEntity = null;
    FirebaseToken firebaseToken = null;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        //This is not working
        List<GrantedAuthority> list = new ArrayList<>();
        GrantedAuthority a = new SimpleGrantedAuthority("ROLE_USER");
        list.add(a);
        return list;
    }

    @Override
    public Object getCredentials() {
        return firebaseToken;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return customerEntity;
    }

    @Override
    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    @Override
    public void setAuthenticated(boolean b) throws IllegalArgumentException {
        this.isAuthenticated = b;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrincipal(CustomerEntity customerEntity) {
        this.customerEntity = customerEntity;
    }

    public void setCredential(FirebaseToken firebaseToken) {
        this.firebaseToken = firebaseToken;
    }
}
