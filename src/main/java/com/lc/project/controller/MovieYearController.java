package com.lc.project.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lc.project.common.BaseResponse;
import com.lc.project.common.ErrorCode;
import com.lc.project.common.ResultUtils;
import com.lc.project.exception.BusinessException;
import com.lc.project.model.dto.movie.MovieQueryRequest;
import com.lc.project.model.entity.Movie;
import com.lc.project.model.entity.MovieNation;
import com.lc.project.model.entity.MovieYear;
import com.lc.project.model.entity.MovieYear;
import com.lc.project.service.MoveNationService;
import com.lc.project.service.MovieService;
import com.lc.project.service.MovieYearService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/year")
public class MovieYearController {

    @Resource
    private MovieYearService movieYearService;

    @Resource
    private MovieService movieService;

    @GetMapping("/getMovieYear")
    public BaseResponse<List<MovieYear>> getMovieYear(){
        List<MovieYear> movieTypes = movieYearService.getMovieYear();
        return ResultUtils.success(movieTypes);
    }

    /**
     * 分页获取列表
     *
     * @param movieQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/year/list/page")
    public BaseResponse<Page<MovieYear>> listMovieYearByPage(@RequestBody MovieQueryRequest movieQueryRequest, HttpServletRequest request) {
        if (movieQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = movieQueryRequest.getCurrent();
        long size = movieQueryRequest.getPageSize();
        QueryWrapper<MovieYear> movieTypeQueryWrapper = new QueryWrapper<>();
        movieTypeQueryWrapper.orderByDesc("createTiem");
        Page<MovieYear> page = movieYearService.page(new Page<>(current, size),movieTypeQueryWrapper);
        return ResultUtils.success(page);
    }

    @PostMapping("/year/add")
    public BaseResponse<Boolean> addMovieYear(String yearName, HttpServletRequest request) {
        if (yearName == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<MovieYear> movieTypeQueryWrapper = new QueryWrapper<>();
        movieTypeQueryWrapper.eq("yearName",yearName);
        long count = movieYearService.count(movieTypeQueryWrapper);
        if(count > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"已有该类型");
        }
        MovieYear movieType = new MovieYear();
        movieType.setYearName(yearName);
        return ResultUtils.success(movieYearService.save(movieType));
    }

    @PostMapping("/year/remove")
    public BaseResponse<Boolean> deleteMovieYear(@RequestBody MovieYear movieType, HttpServletRequest request) {
        if (movieType.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<Movie> movieQueryWrapper = new QueryWrapper<>();
        movieQueryWrapper.eq("type",movieType.getId());
        long count = movieService.count(movieQueryWrapper);
        if(count > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请先删除相关类型影视");
        }
        Integer state = movieType.getState();
        if(state.equals(1)){
            state = 0;
        }else {
            state = 1;
        }
        movieType.setState(state);
        return ResultUtils.success(movieYearService.updateById(movieType));
    }

    @PostMapping("/year/update")
    public BaseResponse<Boolean> updateMovieYear(@RequestBody MovieYear movieType, HttpServletRequest request) {
        if (movieType == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<MovieYear> movieQueryWrapper = new QueryWrapper<>();
        movieQueryWrapper.eq("yearName",movieType.getYearName());
        long count = movieYearService.count(movieQueryWrapper);
        if(count > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"已有此类型影视");
        }
        return ResultUtils.success(movieYearService.updateById(movieType));
    }

    @PostMapping("/type/get")
    public BaseResponse<MovieYear> updateMovieYear(Integer id, HttpServletRequest request) {
        if (id == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(movieYearService.getById(id));
    }



}
