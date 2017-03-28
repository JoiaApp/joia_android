package com.joiaapp.joia.dto;

/**
 * Created by arnell on 3/27/2017.
 * Copyright 2017 Joia. All rights reserved.
 */

public class Group {
    private Integer id;
    private String name;
    private String guid;
    private String password;
    private String owner_id;
    private String business_id;
    private String created_at;
    private String updated_at;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOwnerId() {
        return owner_id;
    }

    public void setOwnerId(String owner_id) {
        this.owner_id = owner_id;
    }

    public String getBusinessId() {
        return business_id;
    }

    public void setBusinessId(String business_id) {
        this.business_id = business_id;
    }

    public String getCreatedAt() {
        return created_at;
    }

    public void setCreatedAt(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdatedAt() {
        return updated_at;
    }

    public void setUpdatedAt(String updated_at) {
        this.updated_at = updated_at;
    }
}
