package com.github.warriorzz.blog.util;

public class UserData {

    private boolean loggedIn;
    private Role role;
    private String id;

    public UserData(boolean loggedIn, Role role, String id){
        this.loggedIn = loggedIn;
        this.role = role;
        this.id = id;
    }

    public boolean isLoggedIn(){ return loggedIn; }
    public Role getRole(){ return role; }

    public void setLoggedIn(boolean loggedIn){ this.loggedIn = loggedIn; }
    public void setRole(Role role){ this.role = role; }
    public String getId() { return id; }

    public enum Role {
        ADMIN, USER, UNASSIGNED;

        public static Role fromString(String string){
            if(string.equalsIgnoreCase("admin")) return ADMIN;
            if(string.equalsIgnoreCase("user")) return USER;
            return UNASSIGNED;
        }
    }

    public static class UserLogin {

        private final String password;
        private Role role = Role.UNASSIGNED;
        private final String username;

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public Role getRole() {
            return role;
        }

        public void setRole(Role role) {
            this.role = role;
        }

        public UserLogin(String username, String password) {
            this.password = password;
            this.username = username;
        }

    }
}
