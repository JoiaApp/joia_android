package com.joiaapp.joia.dto.request;

/**
 * Created by arnell on 9/1/2017.
 * Copyright 2017 Joia. All rights reserved.
 */

public class InviteRequest {
    private String email;
    private boolean isMention;

    public InviteRequest(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isMention() {
        return isMention;
    }

    public void setMention(boolean mention) {
        isMention = mention;
    }
}
