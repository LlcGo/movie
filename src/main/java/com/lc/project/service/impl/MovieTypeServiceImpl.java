package com.lc.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lc.project.model.entity.MovieType;
import com.lc.project.service.impl.MovieTypeService;
import com.lc.project.mapper.MovieTypeMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author asus
* @description 针对表【movie_type(电影类型)】的数据库操作Service实现
* @createDate 2024-01-24 22:24:21
*/
@Service
public class MovieTypeServiceImpl extends ServiceImpl<MovieTypeMapper, MovieType>
    implements MovieTypeService{

    @Override
    public List<MovieType> getMovieType() {
       return this.list();
    }
}




