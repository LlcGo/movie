package com.lc.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lc.project.model.entity.MovieYear;
import com.lc.project.service.MovieYearService;
import com.lc.project.mapper.MovieYearMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author asus
* @description 针对表【movie_year】的数据库操作Service实现
* @createDate 2024-01-25 00:17:43
*/
@Service
public class MovieYearServiceImpl extends ServiceImpl<MovieYearMapper, MovieYear>
    implements MovieYearService{

    @Override
    public List<MovieYear> getMovieYear() {
        QueryWrapper<MovieYear> movieYearQueryWrapper = new QueryWrapper<>();
        movieYearQueryWrapper.eq("state",0);
        return this.list(movieYearQueryWrapper);
    }
}




