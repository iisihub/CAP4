package com.iisigroup.cap.jwt;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.iisigroup.cap.security.model.Role;

//@Data
public class JwtUser implements com.iisigroup.cap.security.model.User {
    // @Id
    private String id;

    // @Indexed(unique = true, direction = IndexDirection.DESCENDING, dropDups = true)
    private String username;

    private String password;
    private String email;
    private Date lastPasswordResetDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getLastPasswordResetDate() {
        return lastPasswordResetDate;
    }

    public void setLastPasswordResetDate(Date lastPasswordResetDate) {
        this.lastPasswordResetDate = lastPasswordResetDate;
    }

    @Override
    public String getCode() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return username;
    }

    @Override
    public String getDepCode() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getStatusDesc() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getUpdater() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Timestamp getUpdateTime() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Locale getLocale() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getStatus() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<? extends Role> getRoles() {
        // TODO Auto-generated method stub
        return null;
    }
}
