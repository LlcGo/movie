package com.lc.project.model.vo;

import lombok.Data;

@Data
public class UserVo {
    private String id;

    private String username;

    private String faceImage;

    private String sex;

    private String likeType;

    private String signature;

    private String nickname;

    private String userRole;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }



}