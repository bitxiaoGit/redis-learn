package com.learnRedis.bean;

import java.io.Serializable;

public class SetReqBean implements Serializable{

    private static final long serialVersionUID = 6121182714463473429L;

    private String user;

    private String friend;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getFriend() {
        return friend;
    }

    public void setFriend(String friend) {
        this.friend = friend;
    }
}
