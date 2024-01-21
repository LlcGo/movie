package com.lc.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lc.project.model.entity.MyFriends;

import java.util.List;

/**
* @author asus
* @description 针对表【my_friends】的数据库操作Mapper
* @createDate 2023-12-31 15:15:59
* @Entity com.lc.project.model.entity.MyFriends
*/
public interface MyFriendsMapper extends BaseMapper<MyFriends> {

    List<MyFriends> getMyFriend(Long myUserId);
}




