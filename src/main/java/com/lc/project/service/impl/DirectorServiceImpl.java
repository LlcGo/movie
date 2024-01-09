package com.lc.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lc.project.mapper.DirectorMapper;
import com.lc.project.model.entity.Director;
import com.lc.project.service.DirectorService;
import org.springframework.stereotype.Service;

/**
* @author asus
* @description 针对表【director(导演表)】的数据库操作Service实现
* @createDate 2024-01-07 18:40:27
*/
@Service
public class DirectorServiceImpl extends ServiceImpl<DirectorMapper, Director>
    implements DirectorService{

}




