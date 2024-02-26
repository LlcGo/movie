package com.lc.project.model.dto.file;

import com.lc.project.common.PageRequest;
import lombok.Data;

import java.io.Serializable;

/**
 * 添加影视
 *
 * @author Lc
 */
@Data
public class VideQueryRequest extends PageRequest implements Serializable {


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