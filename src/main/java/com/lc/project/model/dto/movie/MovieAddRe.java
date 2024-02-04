package com.lc.project.model.dto.movie;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.lc.project.model.entity.MovieNation;
import com.lc.project.model.entity.MovieType;
import com.lc.project.model.entity.MovieYear;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class MovieAddRe implements Serializable {

    private Integer id;

    /**
     * 电影名字
     */
    private String movieName;



    /**
     * 首页大图
     */
    private String bigImg;

}
