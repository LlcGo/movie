package com.lc.project.controller;

import com.github.xiaoymin.knife4j.core.util.StrUtil;
import com.lc.project.common.BaseResponse;
import com.lc.project.common.ErrorCode;
import com.lc.project.common.ResultUtils;
import com.lc.project.exception.BusinessException;
import com.lc.project.model.entity.MyFriends;
import com.lc.project.model.entity.Users;
import com.lc.project.service.MyFriendsService;
import com.lc.project.service.UsersService;
import com.lc.project.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@Slf4j
public class FriendsController {

    @Resource
    private MyFriendsService myFriendsService;

    @Resource
    private UsersService usersService;

    @Resource
    private RedisUtils redisUtils;

    @GetMapping("/get/myFriends")
    public BaseResponse<List<MyFriends>> getMyFriends(@RequestParam("userId") String Id){
        if(Id == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long myUserId = Long.parseLong(Id);
        List<MyFriends> myFriends = myFriendsService.getMyFriends(myUserId);
        return ResultUtils.success(myFriends);
    }

    @PostMapping("/delete/myFriends")
    public BaseResponse<Boolean> deleteMyFriend(String acceptUserId){
        if(StrUtil.isBlank(acceptUserId)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Boolean flag = myFriendsService.deleteFriend(acceptUserId);
        return ResultUtils.success(flag);
    }

    @PostMapping("/removeCurrent")
    public void removeCurrent(){
        Users loginUser = usersService.getLoginUser();
        redisUtils.removeCurrent(loginUser.getId());
    }



}
