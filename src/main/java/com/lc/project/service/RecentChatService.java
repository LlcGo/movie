package com.lc.project.service;

import com.lc.project.common.BaseResponse;
import com.lc.project.model.entity.RecentChat;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lc.project.model.entity.Users;

import java.util.List;

/**
* @author asus
* @description 针对表【recent_chat(近期聊天)】的数据库操作Service
* @createDate 2024-01-20 20:29:59
*/
public interface RecentChatService extends IService<RecentChat> {

    boolean addChat(String acceptUserId);

    List<RecentChat> getRecentChat();


    Boolean toDelete(String acceptUserId);
}
