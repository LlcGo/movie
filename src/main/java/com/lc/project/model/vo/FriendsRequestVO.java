package com.lc.project.model.vo;

import lombok.Data;

@Data
public class FriendsRequestVO {
    private String sendUserId;
    private String sendUsername;
    private String sendFaceImage;
    private String sendNickname;
}