package com.lc.project.model.vo;

import lombok.Data;

import java.util.Date;


public class BarrageVO {

    /**
     * 内容
     */
    private String text;

    /**
     * 弹幕出现时间
     */
    private Double time;

    /**
     * 弹幕颜色
     */
    private String color;


    /**
     * 是否是我发送的弹幕
     */
    private boolean isMe;

    private Date createTime;

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Double getTime() {
        return time;
    }

    public void setTime(Double time) {
        this.time = time;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean getIsMe() {
        return isMe;
    }

    public void setIsMe(boolean isMe) {
        this.isMe = isMe;
    }




//    private boolean force;

}
