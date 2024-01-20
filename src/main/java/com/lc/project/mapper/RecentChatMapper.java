package com.lc.project.mapper;

import com.lc.project.model.entity.RecentChat;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lc.project.model.entity.Users;

import java.util.List;

/**
* @author asus
* @description 针对表【recent_chat(近期聊天)】的数据库操作Mapper
* @createDate 2024-01-20 20:29:59
* @Entity com.lc.project.model.entity.RecentChat
*/
public interface RecentChatMapper extends BaseMapper<RecentChat> {

    /**
     * 查询所有的最近的聊天信息并且获取所有的对方信息
     * @param currentUserId
     * @return
     */
    List<RecentChat> getRecentAndFriends(String currentUserId);

    List<Users> getFriends(String currentUserId);
}




