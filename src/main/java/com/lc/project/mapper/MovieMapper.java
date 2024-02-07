package com.lc.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lc.project.model.entity.Movie;

import java.util.List;

/**
* @author asus
* @description 针对表【movie(电影表)】的数据库操作Mapper
* @createDate 2024-01-06 11:09:38
* @Entity com.lc.project.model.entity.Movie
*/
public interface MovieMapper extends BaseMapper<Movie> {

    List<Movie> getMovieHotListByType(Integer type);

    List<Movie> getMovieHotListByScore(Integer type);

    Movie getMovieAndTypeNameById(long id);

    List<Movie> getMovieIndexListByType(int type);

    List<Movie> getAllHotMovieOrderByHot();

    List<Movie> getAllHotMovieOrderByScore();

    List<Movie> getMovieHotToEChars();

    List<Movie> getMovieScoreToEChars();

    List<Movie> getAllByTypeEChars();
}




