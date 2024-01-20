package com.lc.project.controller;

import com.lc.project.common.BaseResponse;
import com.lc.project.common.ResultUtils;
import com.lc.project.model.entity.RecentChat;
import com.lc.project.model.entity.Users;
import com.lc.project.model.vo.PurchasedVO;
import com.lc.project.service.RecentChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/recentChat")
@Slf4j
public class RecentChatController {

    @Resource
    private RecentChatService recentChatService;

    @PostMapping("/add")
    public BaseResponse<Boolean> addRecentChat(String acceptUserId) {
        boolean save =  recentChatService.addChat(acceptUserId);
        return ResultUtils.success(save);
    }

    @PostMapping("/get")
    public BaseResponse<List<RecentChat>> getRecentChat() {
        List<RecentChat> recentChats =  recentChatService.getRecentChat();
        return ResultUtils.success(recentChats);
    }

    @GetMapping("/delete")
    public BaseResponse<Boolean> deleteRecentChat(String acceptUserId){
        Boolean delete = recentChatService.toDelete(acceptUserId);
        return ResultUtils.success(delete);
    }
}
