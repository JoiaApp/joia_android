package com.joiaapp.joia.dto.request;

import com.joiaapp.joia.dto.User;

/**
 * Created by arnell on 3/20/2017.
 * Copyright 2017 Joia. All rights reserved.
 */

public class CreateUserRequest {
    private User user;

    public CreateUserRequest(User newUser) {
        user = newUser;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
