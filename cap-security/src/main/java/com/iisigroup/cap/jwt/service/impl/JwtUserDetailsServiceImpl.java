package com.iisigroup.cap.jwt.service.impl;

import javax.annotation.Resource;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.iisigroup.cap.security.dao.SecUserDao;
import com.iisigroup.cap.security.model.CapUserDetails;
import com.iisigroup.cap.security.model.User;

//@Service
public class JwtUserDetailsServiceImpl implements UserDetailsService {
    @Resource
    private SecUserDao<? extends User> userDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDao.getUserByLoginId(username, null);

        if (user == null) {
            throw new UsernameNotFoundException(String.format("No user found with username '%s'.", username));
        } else {
            CapUserDetails userdetails = new CapUserDetails();
            userdetails.setPassword(user.getPassword());
            return userdetails;

        }
    }
}
