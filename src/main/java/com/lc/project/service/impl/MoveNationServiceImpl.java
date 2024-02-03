package com.lc.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lc.project.mapper.MoveNationMapper;
import com.lc.project.model.entity.MovieNation;
import com.lc.project.model.entity.MovieType;
import com.lc.project.service.MoveNationService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author asus
* @description 针对表【move_nation】的数据库操作Service实现
* @createDate 2024-01-25 00:17:52
*/
@Service
public class MoveNationServiceImpl extends ServiceImpl<MoveNationMapper, MovieNation>
    implements MoveNationService{

    @Override
    public List<MovieNation> getMovieNation() {
        QueryWrapper<MovieNation> movieNationQueryWrapper = new QueryWrapper<>();
        movieNationQueryWrapper.eq("state",0);
        return this.list(movieNationQueryWrapper);
    }
}




