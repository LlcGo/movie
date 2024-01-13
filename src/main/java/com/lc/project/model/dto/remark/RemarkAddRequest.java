package com.lc.project.model.dto.remark;

import lombok.Data;

import java.io.Serializable;

/**
 * 订单请求
 *
 * @author Lc
 */
@Data
public class RemarkAddRequest implements Serializable {


    /**
     *
     */
    private String userId;

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



}