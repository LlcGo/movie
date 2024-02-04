package com.lc.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lc.project.model.entity.Barrage;

import java.util.List;

/**
* @author asus
* @description 针对表【barrage(弹幕表)】的数据库操作Mapper
* @createDate 2024-01-06 11:09:18
* @Entity com.lc.project.model.entity.Barrage
*/
public interface BarrageMapper extends BaseMapper<Barrage> {

    List<Barrage> getListByNickNameAndMovieName(long pageSize, long current, String movieName, String nickName, String content);

    Integer countListByNickNameAndMovieName(String movieName, String nickName, String content);
}




