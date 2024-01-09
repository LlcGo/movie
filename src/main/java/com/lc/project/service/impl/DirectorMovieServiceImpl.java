package com.lc.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lc.project.mapper.DirectorMovieMapper;
import com.lc.project.model.entity.DirectorMovie;
import com.lc.project.service.DirectorMovieService;
import org.springframework.stereotype.Service;

/**
* @author asus
* @description 针对表【director_movie(导演与电影关系表)】的数据库操作Service实现
* @createDate 2024-01-06 11:09:26
*/
@Service
public class DirectorMovieServiceImpl extends ServiceImpl<DirectorMovieMapper, DirectorMovie>
    implements DirectorMovieService{

}




