package com.lc.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lc.project.mapper.ActorMovieMapper;
import com.lc.project.model.entity.ActorMovie;
import com.lc.project.service.ActorMovieService;
import org.springframework.stereotype.Service;

/**
* @author asus
* @description 针对表【actor_movie(演员与电影关系表)】的数据库操作Service实现
* @createDate 2024-01-06 11:08:08
*/
@Service
public class ActorMovieServiceImpl extends ServiceImpl<ActorMovieMapper, ActorMovie>
    implements ActorMovieService{

}




