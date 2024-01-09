package com.lc.project.model.dto.security;

import lombok.Data;

/**
 * 二维码
 */
@Data
public class QCCode {
    private String loginVerifyCodeRandom;
    private String picCode;
}
