package com.example.demo;

import com.example.demo.repository.CustomerRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//https://medium.com/@purikunal22/securing-springboot-api-using-firebase-authentication-16d72dd250cc
@Component
public class RequestFilter extends OncePerRequestFilter {
    @Autowired
    CustomerRepository customerRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String requestToken = httpServletRequest.getHeader("Authorization");
        UserAuthentication authentication = new UserAuthentication();

        if (requestToken != null) {

            try {

                FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(requestToken);
                String uid = decodedToken.getUid();
                authentication.setName(uid);
                authentication.setPrincipal(customerRepository.findByUid(uid));
                authentication.setAuthenticated(decodedToken.isEmailVerified());
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (FirebaseAuthException e) {
                e.printStackTrace();
            }
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
