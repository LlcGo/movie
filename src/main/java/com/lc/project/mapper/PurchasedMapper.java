package com.lc.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lc.project.model.entity.Purchased;

import java.util.List;

/**
* @author asus
* @description 针对表【purchased(已购买电影表)】的数据库操作Mapper
* @createDate 2024-01-06 11:09:45
* @Entity com.lc.project.model.entity.Purchased
*/
public interface PurchasedMapper extends BaseMapper<Purchased> {

    List<Purchased> getPurchAndMovieByUserId(String id);
}




