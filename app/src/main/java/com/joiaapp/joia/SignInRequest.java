package com.joiaapp.joia;

/**
 * Created by arnell on 1/11/2017.
 * Copyright 2017 Joia. All rights reserved.
 */

public class SignInRequest {
    private String email;
    private String password;

    public SignInRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
