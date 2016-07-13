package com.softdesign.devintensive.data.network.req;

/**
 * Class for sending authentication request to server like POJO.
 */
public class UserLoginReq {

    private String email;
    private String password;

    public UserLoginReq(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
