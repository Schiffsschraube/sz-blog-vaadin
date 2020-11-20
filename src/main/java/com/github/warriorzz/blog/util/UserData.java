package com.github.warriorzz.blog.util;

public class UserData {

    private boolean loggedIn = false;
    private Role role = Role.USER;

    public UserData(boolean loggedIn, Role role){
        this.loggedIn = loggedIn;
        this.role = role;
    }

    public boolean isLoggedIn(){ return loggedIn; }
    public Role getRole(){ return role; }

    public void setLoggedIn(boolean loggedIn){ this.loggedIn = loggedIn; }
    public void setRole(Role role){ this.role = role; }

    public enum Role {
        ADMIN, USER;
    }
}
