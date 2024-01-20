package com.lc.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.xiaoymin.knife4j.core.util.StrUtil;
import com.lc.project.common.ErrorCode;
import com.lc.project.exception.BusinessException;
import com.lc.project.model.entity.RecentChat;
import com.lc.project.model.entity.Users;
import com.lc.project.service.RecentChatService;
import com.lc.project.mapper.RecentChatMapper;
import com.lc.project.service.UsersService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
* @author asus
* @description 针对表【recent_chat(近期聊天)】的数据库操作Service实现
* @createDate 2024-01-20 20:29:59
*/
@Service
public class RecentChatServiceImpl extends ServiceImpl<RecentChatMapper, RecentChat>
    implements RecentChatService{

    @Resource
    private UsersService usersService;

    @Resource
    private RecentChatMapper recentChatMapper;

    @Override
    public boolean addChat(String acceptUserId) {
        if(StrUtil.isBlank(acceptUserId)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Users loginUser = usersService.getLoginUser();
        String currentUserId = loginUser.getId();
        RecentChat recentChat = new RecentChat();
        QueryWrapper<RecentChat> recentChatQueryWrapper = new QueryWrapper<>();
        recentChatQueryWrapper.eq("userId",currentUserId);
        recentChatQueryWrapper.eq("acceptUserId",acceptUserId);
        long count = this.count(recentChatQueryWrapper);
        if (count > 0){
            //如果已经存在这条聊天记录直接返回不插入新的数据
            return true;
        }
        recentChat.setAcceptUserId(acceptUserId);
        recentChat.setUserId(currentUserId);
        return this.save(recentChat);
    }

    @Override
    public List<RecentChat> getRecentChat() {
        Users loginUser = usersService.getLoginUser();
        String currentUserId = loginUser.getId();
        return recentChatMapper.getRecentAndFriends(currentUserId);

    }

    @Override
    public Boolean toDelete(String acceptUserId) {
        Users loginUser = usersService.getLoginUser();
        QueryWrapper<RecentChat> recentChatQueryWrapper = new QueryWrapper<>();
        recentChatQueryWrapper.eq("userId",loginUser.getId());
        recentChatQueryWrapper.eq("acceptUserId",acceptUserId);
        return this.remove(recentChatQueryWrapper);
    }

}




