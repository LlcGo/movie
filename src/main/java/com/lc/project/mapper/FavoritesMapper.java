package com.lc.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lc.project.model.entity.Favorites;

import java.util.List;

/**
* @author asus
* @description 针对表【favorites(收藏表)】的数据库操作Mapper
* @createDate 2024-01-06 11:09:32
* @Entity com.lc.project.model.entity.Favorites
*/
public interface FavoritesMapper extends BaseMapper<Favorites> {

    List<Favorites> getMyFavoritesAndMovieByUserId(String id);
}




