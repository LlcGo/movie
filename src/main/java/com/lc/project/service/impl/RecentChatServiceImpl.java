package com.lc.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lc.project.model.entity.RecentChat;
import com.lc.project.service.RecentChatService;
import com.lc.project.mapper.RecentChatMapper;
import org.springframework.stereotype.Service;

/**
* @author asus
* @description 针对表【recent_chat(近期聊天)】的数据库操作Service实现
* @createDate 2024-01-20 20:29:59
*/
@Service
public class RecentChatServiceImpl extends ServiceImpl<RecentChatMapper, RecentChat>
    implements RecentChatService{

}




