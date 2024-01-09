package com.lc.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lc.project.model.entity.ChatMsg;

import java.util.List;

/**
* @author asus
* @description 针对表【chat_msg】的数据库操作Service
* @createDate 2023-12-31 15:13:39
*/
public interface ChatMsgService extends IService<ChatMsg> {

    List<ChatMsg> getUsersChat(Long userId, Long otherUserId);

    List<ChatMsg> getUnread(long sendUserId);


    Boolean readMessage(long sendUserId, long rOtherUserId);
}
