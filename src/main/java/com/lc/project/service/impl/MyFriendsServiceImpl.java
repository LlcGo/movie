package com.lc.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lc.project.mapper.MyFriendsMapper;
import com.lc.project.model.entity.MyFriends;
import com.lc.project.model.entity.Users;
import com.lc.project.service.MyFriendsService;
import com.lc.project.service.UsersService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author asus
* @description 针对表【my_friends】的数据库操作Service实现
* @createDate 2023-12-31 15:15:59
*/
@Service
public class MyFriendsServiceImpl extends ServiceImpl<MyFriendsMapper, MyFriends>
    implements MyFriendsService{

    @Resource
    private UsersService usersService;

    public List<Users> getMyFriends(Long myUserId){
        QueryWrapper<MyFriends> myFriendsQueryWrapper = new QueryWrapper<>();
        myFriendsQueryWrapper.eq("myUserId",myUserId);
        List<MyFriends> list = this.list(myFriendsQueryWrapper);
        //获得所有朋友的id
        List<String> myFriendId = list.stream().map(MyFriends::getMyFriendUserId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(myFriendId)){
            return new ArrayList<Users>();
        }
        //查询所有朋友的信息
        return usersService.listByIds(myFriendId);
    }

    @Override
    public Boolean deleteFriend(String acceptUserId) {
        Users loginUser = usersService.getLoginUser();
        String currentUserId = loginUser.getId();
        QueryWrapper<MyFriends> myFriendsQueryWrapper = new QueryWrapper<>();
        myFriendsQueryWrapper.eq("myUserId",currentUserId);
        myFriendsQueryWrapper.eq("myFriendUserId",acceptUserId);
        UpdateWrapper<MyFriends> myFriendsUpdateWrapper = new UpdateWrapper<>();
        //将对方的 state 设置 为 1 代表 让对方知道你的好友已经将你删除
        myFriendsUpdateWrapper.eq("myUserId",acceptUserId);
        myFriendsUpdateWrapper.eq("myFriendUserId",currentUserId);
        myFriendsUpdateWrapper.set("state",1);
        this.update(myFriendsUpdateWrapper);
        return this.remove(myFriendsQueryWrapper);
    }
}




