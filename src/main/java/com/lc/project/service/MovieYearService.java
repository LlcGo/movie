package com.lc.project.service;

import com.lc.project.model.entity.MovieYear;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author asus
* @description 针对表【movie_year】的数据库操作Service
* @createDate 2024-01-25 00:17:43
*/
public interface MovieYearService extends IService<MovieYear> {

    List<MovieYear> getMovieYear();

}
