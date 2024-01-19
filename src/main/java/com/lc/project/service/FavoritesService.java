package com.lc.project.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lc.project.model.dto.favorites.FavoritesQueryRequest;
import com.lc.project.model.entity.Favorites;
import com.lc.project.model.vo.FavoritesVo;

import java.util.List;

/**
* @author asus
* @description 针对表【favorites(收藏表)】的数据库操作Service
* @createDate 2024-01-06 11:09:32
*/
public interface FavoritesService extends IService<Favorites> {

    void validFavorites(Favorites favorites, boolean add);

    Integer toAddFavorites(Favorites favorites);

    boolean removeFavoritesById(long id);

    boolean toUpdate(Favorites favorites);

    Favorites getFavoritesById(long id);

    List<Favorites> getListFavorites(Favorites favoritesQuery);

    Page<FavoritesVo> listPage(FavoritesQueryRequest favoritesQueryRequest);
}
