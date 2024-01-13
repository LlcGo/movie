package com.lc.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lc.project.model.entity.RemarkUser;
import com.lc.project.service.RemarkUserService;
import com.lc.project.mapper.RemarkUserMapper;
import org.springframework.stereotype.Service;

/**
* @author asus
* @description 针对表【remark_user(用户对评论是否支持)】的数据库操作Service实现
* @createDate 2024-01-13 19:34:13
*/
@Service
public class RemarkUserServiceImpl extends ServiceImpl<RemarkUserMapper, RemarkUser>
    implements RemarkUserService{

}




