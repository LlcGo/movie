package com.lc.project.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lc.project.annotation.AuthCheck;
import com.lc.project.common.BaseResponse;
import com.lc.project.common.DeleteRequest;
import com.lc.project.common.ErrorCode;
import com.lc.project.common.ResultUtils;
import com.lc.project.exception.BusinessException;
import com.lc.project.model.dto.movie.MovieAddRequest;
import com.lc.project.model.dto.movie.MovieQueryRequest;
import com.lc.project.model.dto.movie.MovieUpdateRequest;
import com.lc.project.model.entity.Movie;
import com.lc.project.model.entity.Users;
import com.lc.project.model.vo.MovieVo;
import com.lc.project.service.MovieService;
import com.lc.project.service.UsersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 電影
 *
 * @author Lc
 */
@RestController
@RequestMapping("/movie")
@Slf4j
public class MovieController {

    @Resource
    private MovieService movieService;

    @Resource
    private UsersService userService;


    /**
     * 创建
     *
     * @param movieAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Integer> addMovie(@RequestBody MovieAddRequest movieAddRequest, HttpServletRequest request) {
        if (movieAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Movie movie = new Movie();
        BeanUtils.copyProperties(movieAddRequest, movie);
        // 校验
        movieService.validMovie(movie, true);
        Users loginUser = userService.getLoginUser(request);
        movie.setUserId(loginUser.getId());
        Integer newMovieId =  movieService.toAddMovie(movie);
        return ResultUtils.success(newMovieId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteMovie(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = deleteRequest.getId();
        // 判断是否存在
        Movie oldMovie = movieService.getById(id);
        if (oldMovie == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可删除
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = movieService.removeMovieById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新
     *
     * @param movieUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateMovie(@RequestBody MovieUpdateRequest movieUpdateRequest,
                                            HttpServletRequest request) {
        if (movieUpdateRequest == null || movieUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //管理员可修改
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        Movie movie = new Movie();
        BeanUtils.copyProperties(movieUpdateRequest, movie);
        // 参数校验
        movieService.validMovie(movie, false);
        boolean result = movieService.toUpdate(movie);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<MovieVo> getMovieById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        MovieVo movie = movieService.getMovieById(id);
        return ResultUtils.success(movie);
    }

    /**
     * 获取列表（仅管理员可使用）
     *
     * @param movieQueryRequest
     * @return
     */
    @AuthCheck(mustRole = "admin")
    @GetMapping("/list")
    public BaseResponse<List<Movie>> listMovie(MovieQueryRequest movieQueryRequest) {
        Movie movieQuery = new Movie();
        if (movieQueryRequest != null) {
            BeanUtils.copyProperties(movieQueryRequest, movieQuery);
        }
        List<Movie> movieList = movieService.getListMovie(movieQuery);
        return ResultUtils.success(movieList);
    }

    /**
     * 分页获取列表
     *
     * @param movieQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<Movie>> listMovieByPage(MovieQueryRequest movieQueryRequest, HttpServletRequest request) {
        if (movieQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Page<Movie> moviePage = movieService.listPage(movieQueryRequest);
        return ResultUtils.success(moviePage);
    }

    @PostMapping("/hot")
    public void addHot(@RequestBody DeleteRequest movie){
        if(movie == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = movie.getId();
        if(id < 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        movieService.increaseHot(id.intValue());
    }


}
