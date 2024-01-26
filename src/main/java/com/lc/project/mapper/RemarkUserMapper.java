package com.lc.project.mapper;

import com.lc.project.model.entity.Remark;
import com.lc.project.model.entity.RemarkUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author asus
* @description 针对表【remark_user(用户对评论是否支持)】的数据库操作Mapper
* @createDate 2024-01-13 19:34:13
* @Entity com.lc.project.model.entity.RemarkUser
*/
public interface RemarkUserMapper extends BaseMapper<RemarkUser> {

    List<Remark> getRemarkAndUserPage(Integer movieId, long current, long pageSize);
}




