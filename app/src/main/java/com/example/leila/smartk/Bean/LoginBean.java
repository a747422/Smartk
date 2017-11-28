package com.example.leila.smartk.Bean;

/**
 * Created by Leila on 2017/11/3.
 */

public class LoginBean {

    /**
     * type :
     * user_name : admin
     */

    private String user_name;
    private String type;

    public LoginBean(String user_name, String type) {
        this.user_name = user_name;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

}
