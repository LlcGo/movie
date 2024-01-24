package com.lc.project.controller;

import com.github.xiaoymin.knife4j.core.util.StrUtil;
import com.lc.project.common.BaseResponse;
import com.lc.project.common.ErrorCode;
import com.lc.project.common.ResultUtils;
import com.lc.project.exception.BusinessException;
import com.lc.project.model.entity.MovieType;
import com.lc.project.model.entity.MyFriends;
import com.lc.project.model.entity.Users;
import com.lc.project.service.MyFriendsService;
import com.lc.project.service.UsersService;
import com.lc.project.service.impl.MovieTypeService;
import com.lc.project.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@Slf4j
public class MovieTyepController {

    @Resource
    private MovieTypeService movieTypeService;

    @GetMapping("/getMovieType")
    public BaseResponse<List<MovieType>> getMovieType(){
        List<MovieType> movieTypes = movieTypeService.getMovieType();
        return ResultUtils.success(movieTypes);
    }




}
