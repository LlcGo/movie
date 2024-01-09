package com.lc.project.controller;

import com.lc.project.common.BaseResponse;
import com.lc.project.common.ErrorCode;
import com.lc.project.common.ResultUtils;
import com.lc.project.exception.BusinessException;
import com.lc.project.model.entity.Users;
import com.lc.project.service.MyFriendsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@Slf4j
public class FriendsController {

    @Resource
    private MyFriendsService myFriendsService;

    @GetMapping("/get/myFriends")
    public BaseResponse<List<Users>> getMyFriends(@RequestParam("userId") String Id){
        if(Id == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long myUserId = Long.parseLong(Id);
        List<Users> myFriends = myFriendsService.getMyFriends(myUserId);
        return ResultUtils.success(myFriends);
    }

}
