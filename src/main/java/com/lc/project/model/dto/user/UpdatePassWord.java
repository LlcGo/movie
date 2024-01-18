package com.lc.project.model.dto.user;

import lombok.Data;

@Data
public class UpdatePassWord {
    private String oldPassword;

    private String newPassword;
}
