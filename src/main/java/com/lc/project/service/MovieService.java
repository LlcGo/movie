package com.lc.project.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lc.project.model.dto.movie.MovieAddRe;
import com.lc.project.model.dto.movie.MovieQueryRequest;
import com.lc.project.model.entity.Movie;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

    Movie getMovieById(long id);

    boolean toUpdate(Movie movie);

    boolean removeMovieById(long id);

    Integer toAddMovie(Movie movie);

    /**
     * 热点增加
     * @param id
     */
    void increaseHot(Integer id);

    ConcurrentHashMap<Integer,List<Movie>> listIndexPage();

    List<Movie> getHotByType(Integer type);

    Movie getMovieAndTypeNameById(long id);

    /**
     * 获得所有的电影热度进行排行
     * @return
     */
    List<Movie> getHotMovie();

    Boolean setState(Integer state, Integer movieId, Boolean flag);

    Boolean setMf(Integer state, Integer movieId);

    Boolean setPrice(Integer price, Integer movieId);

    /**
     * 首页推荐 存入redis 与 mysql
     *
     * @param movie
     * @param state
     * @return
     */
    Boolean SyRe(MovieAddRe movie, String state);

    Map<Object, Object> getRe();
}
