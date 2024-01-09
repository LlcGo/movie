package com.lc.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lc.project.mapper.MyFriendsMapper;
import com.lc.project.model.entity.MyFriends;
import com.lc.project.model.entity.Users;
import com.lc.project.service.MyFriendsService;
import com.lc.project.service.UsersService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
        myFriendsQueryWrapper.eq("my_user_id",myUserId);
        List<MyFriends> list = this.list(myFriendsQueryWrapper);
        //获得所有朋友的id
        List<String> myFriendId = list.stream().map(MyFriends::getMyFriendUserId).collect(Collectors.toList());
        //查询所有朋友的信息
        return usersService.listByIds(myFriendId);
    }
}




