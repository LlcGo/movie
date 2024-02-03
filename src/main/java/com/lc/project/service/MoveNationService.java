package com.lc.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lc.project.model.entity.MovieNation;
import com.lc.project.model.entity.MovieType;

import java.util.List;

/**
* @author asus
* @description 针对表【move_nation】的数据库操作Service
* @createDate 2024-01-25 00:17:52
*/
public interface MoveNationService extends IService<MovieNation> {

    List<MovieNation> getMovieNation();

}
