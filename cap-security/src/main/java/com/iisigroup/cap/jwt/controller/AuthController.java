package com.iisigroup.cap.jwt.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.iisigroup.cap.jwt.JwtAuthenticationRequest;
import com.iisigroup.cap.jwt.JwtAuthenticationResponse;
import com.iisigroup.cap.jwt.JwtUser;
import com.iisigroup.cap.jwt.service.AuthService;

@RestController
public class AuthController {
    @Value("${jwt.header:#{null}}")
    private String tokenHeader;

    @Resource
    private AuthService authService;

    @RequestMapping(value = "${jwt.route.authentication.path:#{null}}", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtAuthenticationRequest authenticationRequest) throws AuthenticationException {
        final String token = authService.login(authenticationRequest.getUsername(), authenticationRequest.getPassword());

        // Return the token
        return ResponseEntity.ok(new JwtAuthenticationResponse(token));
    }

    @RequestMapping(value = "${jwt.route.authentication.refresh:#{null}}", method = RequestMethod.GET)
    public ResponseEntity<?> refreshAndGetAuthenticationToken(HttpServletRequest request) throws AuthenticationException {
        String token = request.getHeader(tokenHeader);
        String refreshedToken = authService.refresh(token);
        if (refreshedToken == null) {
            return ResponseEntity.badRequest().body(null);
        } else {
            return ResponseEntity.ok(new JwtAuthenticationResponse(refreshedToken));
        }
    }

    @RequestMapping(value = "${jwt.route.authentication.register:#{null}}", method = RequestMethod.POST)
    public JwtUser register(@RequestBody JwtUser addedUser) throws AuthenticationException {
        return authService.register(addedUser);
    }
}
