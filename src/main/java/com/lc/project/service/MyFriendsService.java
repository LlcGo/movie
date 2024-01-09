package com.lc.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lc.project.model.entity.MyFriends;
import com.lc.project.model.entity.Users;

import java.util.List;

/**
* @author asus
* @description 针对表【my_friends】的数据库操作Service
* @createDate 2023-12-31 15:15:59
*/
public interface MyFriendsService extends IService<MyFriends> {

    public List<Users> getMyFriends(Long myId);
}
