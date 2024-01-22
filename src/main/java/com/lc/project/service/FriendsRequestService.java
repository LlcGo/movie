package com.lc.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lc.project.model.entity.FriendsRequest;

import java.util.List;

/**
* @author asus
* @description 针对表【friends_request】的数据库操作Service
* @createDate 2023-12-31 15:15:05
*/
public interface FriendsRequestService extends IService<FriendsRequest> {

    Boolean requestFriends(String acceptUserId);

    Boolean agreeFriend(String sendUserId, String requestId);

    Boolean deleteFriend(String requestId);

    List<FriendsRequest> getMyRequest();

    /**
     * 拒绝请求
     * @param requestId
     * @param acceptUserId
     * @return
     */
    Boolean rejectFriend(String requestId, String acceptUserId);

    Integer setReadMessage();
}
