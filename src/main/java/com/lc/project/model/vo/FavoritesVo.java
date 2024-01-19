package com.lc.project.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.lc.project.model.entity.Movie;
import com.lc.project.model.entity.Users;
import lombok.Data;

import java.util.Date;

@Data
public class FavoritesVo {
    /**
     *
     */
    private Integer id;

    /**
     *
     */
    private String userId;

    /**
     *
     */
    private Movie movie;

    /**
     *
     */
    private Date createTime;

    /**
     *
     */
    private Date updateTime;

    /**
     * 0 未删除 1 已删除
     */
    private Integer isDelete;



}
