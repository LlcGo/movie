package com.lc.project.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.lc.project.model.entity.Users;
import lombok.Data;

import java.util.Date;

@Data
public class RemarkVo {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     *
     */
    private Users user;

    /**
     *
     */
    private Integer movieId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 评分
     */
    private Integer score;

    /**
     *
     */
    private Date createTime;

    /**
     *
     */
    private Date updateTime;

    /**
     * 这个评论喜欢的人的数量
     */
    private Integer liked;

    /**
     * 这个评论不喜欢的人的数量
     */
    private Integer disLiked;


}
