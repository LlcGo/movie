package com.lc.project.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.lc.project.model.entity.Actors;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class MovieVo {
    /**
     * 电影标识Id
     */
    private Integer id;

    /**
     * 电影名字
     */
    private String movieName;

    /**
     * 电影类型
     */
    private Integer type;

    /**
     * 0 中国
     1 美国
     2 中国香港
     3 韩国
     4 日本
     5 法国
     6 英国
     7 德国
     8 泰国
     9 印度
     */
    private String nation;

    /**
     * 电影年代
     0 2023
     1 2022
     2 2021
     3 2020
     4 2019
     5 2018
     6 2017
     7 2016
     8 2015
     9 2014
     10 2013
     11 2012
     12 2011
     13 2010
     14 00年代（2000-2009）
     15 90年代（1990-1999）
     16 更早（1979之前）
     */
    private Integer year;

    /**
     * 图片位置
     */
    private String img;



    /**
     * 电影简介
     */
    private String movieProfile;

    /**
     * 视频为id
     */
    private Integer videoId;

    /**
     * 那個管理員上架的
     */
    private String userId;

    /**
     * 创建时间
     */
    private Date creatTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 电影状态 0未上架 1上架 2 3
     */
    private Integer state;

    /**
     * 0 未删除 1 已删除
     */
    private Integer isDelete;

    /**
     * 评分
     */
    private Double score;

    /**
     * 演员
     */
    private String actorsName;

    /**
     * 导演
     */
    private String directorName;

    /**
     * favorite
     */
    private Boolean favorite;

    /**
     * 价格
     */
    private Integer price;
}
