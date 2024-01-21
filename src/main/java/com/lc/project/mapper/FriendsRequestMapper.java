package com.lc.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lc.project.model.entity.FriendsRequest;

import java.util.List;

/**
* @author asus
* @description 针对表【friends_request】的数据库操作Mapper
* @createDate 2023-12-31 15:15:05
* @Entity com.lc.project.model.entity.FriendsRequest
*/
public interface FriendsRequestMapper extends BaseMapper<FriendsRequest> {

    List<FriendsRequest> getRequestByUserId(String currentUserId);

    List<FriendsRequest> getReceiveByUserId(String currentUserId);
}




