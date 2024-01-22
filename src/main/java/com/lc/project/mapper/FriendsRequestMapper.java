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

    /**
     * 设置关于用户的所有的之前离线未读到拒绝的消息 为 1
     * @param currentId
     * @return
     */
    Integer updateRequestMessageToOne(String currentId);

    /**
     * 设置关于用户的所有的之前离线未读到同意的消息 为 2
     * @param currentId
     * @return
     */
    Integer updateRequestMessageToTwo(String currentId);

    Integer updateRequestLineToOne(String currentId);

    Integer updateRequestLineToTwo(String currentId);
}




