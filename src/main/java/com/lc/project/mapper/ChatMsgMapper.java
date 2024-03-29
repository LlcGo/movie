package com.lc.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lc.project.model.entity.ChatMsg;

import java.util.List;

/**
* @author asus
* @description 针对表【chat_msg】的数据库操作Mapper
* @createDate 2023-12-31 15:13:39
* @Entity com.lc.project.model.entity.ChatMsg
*/
public interface ChatMsgMapper extends BaseMapper<ChatMsg> {

    int updateByMyIdAndOtherId(long sendUserId, long rOtherUserId);

    List<ChatMsg> getChatAndUsers(Long userId, Long otherUserId);

    List<ChatMsg> getChatAndOtherUsers(Long userId, Long otherUserId);
}




