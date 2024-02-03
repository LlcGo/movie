package com.lc.project.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lc.project.common.BaseResponse;
import com.lc.project.common.ErrorCode;
import com.lc.project.common.ResultUtils;
import com.lc.project.exception.BusinessException;
import com.lc.project.model.dto.movie.MovieQueryRequest;
import com.lc.project.model.entity.Movie;
import com.lc.project.model.entity.MovieType;
import com.lc.project.service.MovieService;
import com.lc.project.service.MovieTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/type")
public class MovieTyepController {

    @Resource
    private MovieTypeService movieTypeService;

    @Resource
    private MovieService movieService;
    @GetMapping("/getMovieType")
    public  BaseResponse<List<MovieType>> getMovieNation(){
        List<MovieType> movieTypes = movieTypeService.getMovieType();
        return ResultUtils.success(movieTypes);
    }

    /**
     * 分页获取列表
     *
     * @param movieQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/type/list/page")
    public BaseResponse<Page<MovieType>> listMovieTypeByPage(@RequestBody MovieQueryRequest movieQueryRequest, HttpServletRequest request) {
        if (movieQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = movieQueryRequest.getCurrent();
        long size = movieQueryRequest.getPageSize();
        QueryWrapper<MovieType> movieTypeQueryWrapper = new QueryWrapper<>();
        movieTypeQueryWrapper.orderByDesc("createTime");
        Page<MovieType> page = movieTypeService.page(new Page<>(current, size),movieTypeQueryWrapper);
        return ResultUtils.success(page);
    }

    @PostMapping("/type/add")
    public BaseResponse<Boolean> addMovieType(String typeName, HttpServletRequest request) {
        if (typeName == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<MovieType> movieTypeQueryWrapper = new QueryWrapper<>();
        movieTypeQueryWrapper.eq("typeName",typeName);
        long count = movieTypeService.count(movieTypeQueryWrapper);
        if(count > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"已有该类型");
        }
        MovieType movieType = new MovieType();
        movieType.setTypeName(typeName);
        return ResultUtils.success(movieTypeService.save(movieType));
    }

    @PostMapping("/type/remove")
    public BaseResponse<Boolean> deleteMovieType(@RequestBody MovieType movieType, HttpServletRequest request) {
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
        return ResultUtils.success(movieTypeService.updateById(movieType));
    }

    @PostMapping("/type/update")
    public BaseResponse<Boolean> updateMovieType(@RequestBody MovieType movieType, HttpServletRequest request) {
        if (movieType == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<MovieType> movieQueryWrapper = new QueryWrapper<>();
        movieQueryWrapper.eq("typeName",movieType.getTypeName());
        long count = movieTypeService.count(movieQueryWrapper);
        if(count > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"已有此类型影视");
        }
        return ResultUtils.success(movieTypeService.updateById(movieType));
    }

    @PostMapping("/type/get")
    public BaseResponse<MovieType> updateMovieType(Integer id, HttpServletRequest request) {
        if (id == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(movieTypeService.getById(id));
    }
}
