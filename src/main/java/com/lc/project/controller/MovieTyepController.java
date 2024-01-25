package com.lc.project.controller;

import com.lc.project.common.BaseResponse;
import com.lc.project.common.ResultUtils;
import com.lc.project.model.entity.MovieType;
import com.lc.project.service.MovieTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@Slf4j
public class MovieTyepController {

    @Resource
    private MovieTypeService movieTypeService;

    @GetMapping("/getMovieType")
    public  BaseResponse<List<MovieType>> getMovieNation(){
        List<MovieType> movieTypes = movieTypeService.getMovieType();
        return ResultUtils.success(movieTypes);
    }
}
