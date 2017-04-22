package com.joiaapp.joia.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arnell on 11/4/2016.
 * Copyright 2017 Joia. All rights reserved.
 */

public class User {
    private Integer id;
    private String email;
    private String name;
    private String password;
    private String role;
    private String image;
    private transient List<Group> groups = new ArrayList<>();


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<Group> getGroups() {
        return groups;
    }
}
