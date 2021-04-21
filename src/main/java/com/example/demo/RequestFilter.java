package com.example.demo;

import com.example.demo.model.CustomerEntity;
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
import java.util.Set;

//https://medium.com/@purikunal22/securing-springboot-api-using-firebase-authentication-16d72dd250cc
@Component
public class RequestFilter extends OncePerRequestFilter {
    @Autowired
    CustomerRepository customerRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String requestToken = request.getHeader("Authorization");

        if (requestToken == null) {
            response.sendError(403, "Authorization header is missing");
            return;
        }
        // Public paths
        Set<String> publicPaths = Set.of("/customer");
        try {
            UserAuthentication authentication = new UserAuthentication();
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(requestToken);
            String uid = decodedToken.getUid();
            authentication.setName(uid);

            CustomerEntity customerEntity = customerRepository.findByUid(uid);
            authentication.setPrincipal(customerEntity);
            authentication.setCredential(decodedToken);
            if (publicPaths.contains(request.getRequestURI())) {
                authentication.setAuthenticated(true);
            } else {
                boolean isAuthenticated = decodedToken.isEmailVerified() && customerRepository.findByUid(uid) != null;
                authentication.setAuthenticated(isAuthenticated);
                if (!isAuthenticated) {
                    response.sendError(403, "Not authorized");
                    return;
                }
            }
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (FirebaseAuthException e) {
            response.sendError(403, "Authorization token is invalid/expired");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
