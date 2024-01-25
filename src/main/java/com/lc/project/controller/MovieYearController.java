package com.lc.project.controller;

import com.lc.project.common.BaseResponse;
import com.lc.project.common.ResultUtils;
import com.lc.project.model.entity.MovieNation;
import com.lc.project.model.entity.MovieYear;
import com.lc.project.service.MoveNationService;
import com.lc.project.service.MovieYearService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@Slf4j
public class MovieYearController {

    @Resource
    private MovieYearService movieYearService;

    @GetMapping("/getMovieYear")
    public BaseResponse<List<MovieYear>> getMovieType(){
        List<MovieYear> movieTypes = movieYearService.getMovieYear();
        return ResultUtils.success(movieTypes);
    }




}
