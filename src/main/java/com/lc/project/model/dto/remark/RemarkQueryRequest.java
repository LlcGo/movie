package com.lc.project.model.dto.remark;

import com.lc.project.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 前端页面展示电影相关下方的评论
 *
 * @author Lc
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RemarkQueryRequest extends PageRequest implements Serializable {



    /**
     * 电影id
     */
    private Integer movieId;

    private String userName;

    private String movieName;

    private String nickName;

    private String content;


    private static final long serialVersionUID = 1L;
}