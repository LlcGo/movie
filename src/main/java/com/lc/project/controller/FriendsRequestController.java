package com.lc.project.controller;

import com.github.xiaoymin.knife4j.core.util.StrUtil;
import com.lc.project.common.BaseResponse;
import com.lc.project.common.ErrorCode;
import com.lc.project.common.ResultUtils;
import com.lc.project.exception.BusinessException;
import com.lc.project.model.entity.FriendsRequest;
import com.lc.project.model.entity.Users;
import com.lc.project.service.FriendsRequestService;
import com.lc.project.service.MyFriendsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/request")
public class FriendsRequestController {

    @Resource
    private FriendsRequestService friendsRequestService;

    @GetMapping("/Friends")
    public BaseResponse<Boolean> requestFriends(String acceptUserId){
        if(acceptUserId == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Boolean request = friendsRequestService.requestFriends(acceptUserId);
        return ResultUtils.success(request);
    }

    /**
     *
     * @param sendUserId 发送方id
     * @return
     */
    @PostMapping("/agree/Friends")
    public BaseResponse<Boolean> agreeFriend(String sendUserId,String requestId){
        if(StrUtil.isBlank(sendUserId)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if(StrUtil.isBlank(requestId)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Boolean flag = friendsRequestService.agreeFriend(sendUserId,requestId);
        return ResultUtils.success(flag);
    }

    @PostMapping("/reject/Friends")
    public BaseResponse<Boolean> rejectFriend(String requestId,String acceptUserId){
        if(StrUtil.isBlank(requestId)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Boolean flag = friendsRequestService.rejectFriend(requestId,acceptUserId);
        return ResultUtils.success(flag);
    }

    @GetMapping("/get/request")
    public BaseResponse<List<FriendsRequest>> getMyRequest(){
        List<FriendsRequest> friendsRequestList = friendsRequestService.getMyRequest();
        return ResultUtils.success(friendsRequestList);
    }

    @GetMapping("/setReadMessage")
    public BaseResponse<Integer> setReadMessage(){
        Integer count = friendsRequestService.setReadMessage();
        return ResultUtils.success(count);
    }

}
