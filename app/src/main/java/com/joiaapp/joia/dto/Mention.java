package com.joiaapp.joia.dto;

import java.io.Serializable;

/**
 * Created by arnell on 4/4/2017.
 * Copyright 2017 Joia. All rights reserved.
 */

public class Mention implements Serializable {
    private int id;
    private int response_id;
    private String created_at;
    private String updated_at;
    private int user_id;
    private User user;

    public Mention(User user) {
        this.user = user;
        this.user_id = user.getId();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getResponse_id() {
        return response_id;
    }

    public void setResponse_id(int response_id) {
        this.response_id = response_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
