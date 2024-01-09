package com.lc.project.controller;

import com.lc.project.common.BaseResponse;
import com.lc.project.common.ErrorCode;
import com.lc.project.common.ResultUtils;
import com.lc.project.exception.BusinessException;
import com.lc.project.model.entity.ChatMsg;
import com.lc.project.service.ChatMsgService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@Slf4j
public class ChatController {

    @Resource
    private ChatMsgService chatMsgService;

    @GetMapping("/getChat")
    public BaseResponse<List<ChatMsg>> getChat(@RequestParam("userId") String userId, @RequestParam("otherUserId")String otherUserId){
        if(userId == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if(otherUserId == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long sendUserId = Long.parseLong(userId);
        long rOtherUserId = Long.parseLong(otherUserId);
        List<ChatMsg> chatMsgList = chatMsgService.getUsersChat(sendUserId,rOtherUserId);
        return ResultUtils.success(chatMsgList);
    }

    @GetMapping("/getUnread")
    public BaseResponse<List<ChatMsg>> getUnread(@RequestParam("userId") String userId){
        if(userId == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
//        if(otherUserId == null){
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
        long sendUserId = Long.parseLong(userId);
//        long rOtherUserId = Long.parseLong(otherUserId);
        List<ChatMsg> chatMsgList = chatMsgService.getUnread(sendUserId);
        return ResultUtils.success(chatMsgList);
    }

    @GetMapping("/readMessage")
    public BaseResponse<Boolean> readMessage(@RequestParam("userId") String userId, @RequestParam("otherUserId")String otherUserId){
        if(userId == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if(otherUserId == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long sendUserId = Long.parseLong(userId);
        long rOtherUserId = Long.parseLong(otherUserId);
        Boolean flag = chatMsgService.readMessage(sendUserId,rOtherUserId);
        return ResultUtils.success(flag);
    }
}
