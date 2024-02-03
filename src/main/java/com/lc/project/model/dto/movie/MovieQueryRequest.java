package com.lc.project.model.dto.movie;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.lc.project.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户查询请求
 *
 * @author Lc
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MovieQueryRequest extends PageRequest implements Serializable {

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
    private Integer nation;

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
     * 电影导演Id
     */
    private Integer directorId;

    /**
     * 电影主演Id
     */
    private Integer actorId;


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
     * 电影状态 0未上架 1上架
     */
    private Integer state;

    /**
     * 0 未删除 1 已删除
     */
    private Integer isDelete;

    /**
     * 是否根据评分来查询
     */
    private Boolean score;

    /**
     * 是否根据热度来查询
     */
    private Boolean hot;

    private static final long serialVersionUID = 1L;
}