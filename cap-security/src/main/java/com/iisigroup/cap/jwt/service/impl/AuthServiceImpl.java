package com.iisigroup.cap.jwt.service.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.iisigroup.cap.jwt.JwtTokenUtil;
import com.iisigroup.cap.jwt.JwtUser;
import com.iisigroup.cap.jwt.service.AuthService;
import com.iisigroup.cap.security.model.CapUserDetails;

@Service
public class AuthServiceImpl implements AuthService {

    @Resource(name = "jwtAuthenticationManager")
    private AuthenticationManager authenticationManager;
    @Resource(name = "jwtUserDetailsServiceImpl")
    private UserDetailsService userDetailsService;
    @Resource
    private JwtTokenUtil jwtTokenUtil;

    @Value("${jwt.tokenHead:#{null}}")
    private String tokenHead;

    @Override
    public JwtUser register(JwtUser userToAdd) {
        return null;
    }

    @Override
    public String login(String username, String password) {
        UsernamePasswordAuthenticationToken upToken = new UsernamePasswordAuthenticationToken(username, password);
        // Perform the security
        final Authentication authentication = authenticationManager.authenticate(upToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Reload password post-security so we can generate token
        final UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        final String token = jwtTokenUtil.generateToken(userDetails);
        return token;
    }

    @Override
    public String refresh(String oldToken) {
        final String token = oldToken.substring(tokenHead.length());
        String username = jwtTokenUtil.getUsernameFromToken(token);
        CapUserDetails user = (CapUserDetails) userDetailsService.loadUserByUsername(username);
        if (jwtTokenUtil.canTokenBeRefreshed(token, (Date) user.getExtraAttrib().get("lastPasswordResetDate"))) {
            return jwtTokenUtil.refreshToken(token);
        }
        return null;
    }
}
