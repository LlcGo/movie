package com.lc.project.controller;


import com.lc.project.common.BaseResponse;
import com.lc.project.common.ErrorCode;
import com.lc.project.common.ResultUtils;
import com.lc.project.exception.BusinessException;
import com.lc.project.model.dto.remark.RemarkUserAddQuery;
import com.lc.project.service.RemarkUserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/remark/user")
public class RemarkAndUserController {

    @Resource
    private RemarkUserService remarkUserService;

    @PostMapping("/like")
    public BaseResponse<Boolean> like(@RequestBody RemarkUserAddQuery remarkUserAddQuery){
       if(remarkUserAddQuery == null){
           throw new BusinessException(ErrorCode.PARAMS_ERROR);
       }
       boolean flag = remarkUserService.like(remarkUserAddQuery);
       return ResultUtils.success(flag);
    }

}
