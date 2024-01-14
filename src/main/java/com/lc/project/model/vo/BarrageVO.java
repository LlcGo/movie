package com.lc.project.model.vo;

import lombok.Data;

@Data
public class BarrageVO {

    /**
     * 内容
     */
    private String text;

    /**
     * 弹幕出现时间
     */
    private String time;

    /**
     * 弹幕颜色
     */
    private String color;


    /**
     * 是否是我发送的弹幕
     */
    private boolean isMe;


    private boolean force;

}
