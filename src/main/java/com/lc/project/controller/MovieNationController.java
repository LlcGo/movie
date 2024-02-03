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
import com.lc.project.service.MoveNationService;
import com.lc.project.service.MovieService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/nation")
public class MovieNationController {

    @Resource
    private MoveNationService moveNationService;

    @Resource
    private MovieService movieService;

    @GetMapping("/getMovieNation")
    public BaseResponse<List<MovieNation>> getMoveNation(){
        List<MovieNation> moveNations = moveNationService.getMovieNation();
        return ResultUtils.success(moveNations);
    }
    
    /**
     * 分页获取列表
     *
     * @param movieQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/nation/list/page")
    public BaseResponse<Page<MovieNation>> listMoveNationByPage(@RequestBody MovieQueryRequest movieQueryRequest, HttpServletRequest request) {
        if (movieQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = movieQueryRequest.getCurrent();
        long size = movieQueryRequest.getPageSize();
        QueryWrapper<MovieNation> moveNationQueryWrapper = new QueryWrapper<>();
        moveNationQueryWrapper.orderByDesc("createTime");
        Page<MovieNation> page = moveNationService.page(new Page<>(current, size),moveNationQueryWrapper);
        return ResultUtils.success(page);
    }

    @PostMapping("/nation/add")
    public BaseResponse<Boolean> addMoveNation(String typeName, HttpServletRequest request) {
        if (typeName == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<MovieNation> moveNationQueryWrapper = new QueryWrapper<>();
        moveNationQueryWrapper.eq("typeName",typeName);
        long count = moveNationService.count(moveNationQueryWrapper);
        if(count > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"已有该类型");
        }
        MovieNation moveNation = new MovieNation();
        moveNation.setNationName(typeName);
        return ResultUtils.success(moveNationService.save(moveNation));
    }

    @PostMapping("/nation/remove")
    public BaseResponse<Boolean> deleteMoveNation(@RequestBody MovieNation moveNation, HttpServletRequest request) {
        if (moveNation.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<Movie> movieQueryWrapper = new QueryWrapper<>();
        movieQueryWrapper.eq("nation",moveNation.getId());
        long count = movieService.count(movieQueryWrapper);
        if(count > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请先删除相关类型影视");
        }
        Integer state = moveNation.getState();
        if(state.equals(1)){
            state = 0;
        }else {
            state = 1;
        }
        moveNation.setState(state);
        return ResultUtils.success(moveNationService.updateById(moveNation));
    }

    @PostMapping("/nation/update")
    public BaseResponse<Boolean> updateMoveNation(@RequestBody MovieNation moveNation, HttpServletRequest request) {
        if (moveNation == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<MovieNation> movieQueryWrapper = new QueryWrapper<>();
        movieQueryWrapper.eq("nationName",moveNation.getNationName());
        long count = moveNationService.count(movieQueryWrapper);
        if(count > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"已有此类型");
        }
        return ResultUtils.success(moveNationService.updateById(moveNation));
    }

    @PostMapping("/nation/get")
    public BaseResponse<MovieNation> updateMoveNation(Integer id, HttpServletRequest request) {
        if (id == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(moveNationService.getById(id));
    }



}
