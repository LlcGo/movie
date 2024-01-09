package com.lc.project.model.dto.netty;

import com.lc.project.model.entity.ChatMsg;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DataContent implements Serializable {
    private Integer action;//动作类型
    private ChatMsg chatMsg;//用户的聊天内容
    private List<ChatMsg> chatMsgList;
    private String extand;//扩展字段
}
