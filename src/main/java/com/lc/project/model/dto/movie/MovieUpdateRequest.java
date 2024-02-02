package com.lc.project.model.dto.movie;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

/**
 * 影视更新
 *
 * @author Lc
 */
@Data
public class MovieUpdateRequest implements Serializable {

    private Integer Id;
    /**
     * 电影名字
     */
    private String movieName;

    /**
     * 电影类型
     */
    private Integer type;


    private Integer nation;


    private Integer year;

    /**
     * 图片位置
     */
    private String img;

    /**
     * 导演名
     */
    private String directorName;


    /**
     * 电影简介
     */
    private String movieProfile;

    /**
     * 视频为id
     */
    private Integer videoId;



    /**
     * 演员名
     */
    private String actorsName;
}