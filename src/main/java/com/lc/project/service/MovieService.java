package com.lc.project.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lc.project.model.dto.movie.MovieQueryRequest;
import com.lc.project.model.entity.Movie;
import com.lc.project.model.vo.MovieVo;

import java.util.List;

/**
* @author asus
* @description 针对表【movie(电影表)】的数据库操作Service
* @createDate 2024-01-06 11:09:38
*/
public interface MovieService extends IService<Movie> {

    /**
     * 檢驗參數
     * @param movie
     * @param add
     */
    void validMovie(Movie movie, boolean add);

    Page<Movie> listPage(MovieQueryRequest movieQueryRequest);

    List<Movie> getListMovie(Movie movieQuery);

    MovieVo getMovieById(long id);

    boolean toUpdate(Movie movie);

    boolean removeMovieById(long id);

    Integer toAddMovie(Movie movie);

    /**
     * 热点增加
     * @param id
     */
    void increaseHot(Integer id);
}
