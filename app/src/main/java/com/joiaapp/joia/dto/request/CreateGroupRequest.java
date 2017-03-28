package com.joiaapp.joia.dto.request;

import com.joiaapp.joia.dto.Group;

/**
 * Created by arnell on 3/28/2017.
 * Copyright 2017 Joia. All rights reserved.
 */

public class CreateGroupRequest {
    private Group group;

    public CreateGroupRequest(Group group) {
        this.group = group;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }
}
