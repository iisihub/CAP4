package com.iisigroup.cap.jwt.service.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.iisigroup.cap.jwt.JwtUser;
import com.iisigroup.cap.security.dao.SecUserDao;
import com.iisigroup.cap.security.model.CapUserDetails;
import com.iisigroup.cap.security.model.User;

//@Service
public class JwtUserDetailsServiceImpl implements UserDetailsService {
    @Resource
    private SecUserDao<? extends User> userDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // FIXME
        // By pass it
        Map<String,String> roles = new HashMap<String,String>();
        roles.put("ROLE_USER", "");
        User user = new JwtUser();
        ((JwtUser)user).setUsername("test");
        CapUserDetails byPassUserdetails = new CapUserDetails(user, "test", roles);
        return byPassUserdetails;

//        User user = userDao.getUserByLoginId(username, null);
//
//        if (user == null) {
//            throw new UsernameNotFoundException(String.format("No user found with username '%s'.", username));
//        } else {
//            CapUserDetails userdetails = new CapUserDetails();
//            userdetails.setPassword(user.getPassword());
//            return userdetails;
//
//        }
    }
}
