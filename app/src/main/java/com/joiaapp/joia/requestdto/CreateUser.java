package com.joiaapp.joia.requestdto;

import com.joiaapp.joia.dto.User;

/**
 * Created by arnell on 3/20/2017.
 * Copyright 2017 Joia. All rights reserved.
 */

public class CreateUser {
    private User user;

    public CreateUser(User newUser) {
        user = newUser;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
