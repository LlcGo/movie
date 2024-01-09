package com.lc.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lc.project.mapper.ActorsMapper;
import com.lc.project.model.entity.Actors;
import com.lc.project.service.ActorsService;
import org.springframework.stereotype.Service;

/**
* @author asus
* @description 针对表【actors(演员表)】的数据库操作Service实现
* @createDate 2024-01-06 11:08:56
*/
@Service
public class ActorsServiceImpl extends ServiceImpl<ActorsMapper, Actors>
    implements ActorsService{

}




