package com.lc.project.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.lc.project.model.entity.Movie;
import lombok.Data;

import java.util.Date;

@Data
public class PurchasedVO {
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



    private static final long serialVersionUID = 1L;

}
