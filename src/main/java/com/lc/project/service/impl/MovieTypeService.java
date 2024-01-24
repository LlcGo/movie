package com.lc.project.service.impl;

import com.lc.project.model.entity.MovieType;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author asus
* @description 针对表【movie_type(电影类型)】的数据库操作Service
* @createDate 2024-01-24 22:24:21
*/
public interface MovieTypeService extends IService<MovieType> {

    List<MovieType> getMovieType();

}
