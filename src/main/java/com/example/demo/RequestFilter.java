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
            response.sendError(401, "Authorization header is missing");
            return;
        }
        // Public paths
        Set<String> publicPaths = Set.of("/customer");
        try {
            UserAuthentication authentication = new UserAuthentication();
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(requestToken);
            String uid = decodedToken.getUid();

            authentication.setName(uid);
            boolean isAuthenticated = decodedToken.isEmailVerified() && customerRepository.findByUid(uid) != null;

            if (!publicPaths.contains(request.getRequestURI()) && !isAuthenticated) {
                response.sendError(401, "Please complete your signup");
                return;
            }
            CustomerEntity customerEntity = customerRepository.findByUid(uid);
            authentication.setPrincipal(customerEntity);
            authentication.setAuthenticated(isAuthenticated);
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (FirebaseAuthException e) {
            response.sendError(401, "Authorization token is invalid");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
