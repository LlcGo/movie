package com.lc.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lc.project.common.ErrorCode;
import com.lc.project.constant.CommonConstant;
import com.lc.project.exception.BusinessException;
import com.lc.project.mapper.FavoritesMapper;
import com.lc.project.model.dto.favorites.FavoritesQueryRequest;
import com.lc.project.model.entity.Favorites;
import com.lc.project.service.FavoritesService;
import com.lc.project.service.UsersService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
* @author asus
* @description 针对表【favorites(收藏表)】的数据库操作Service实现
* @createDate 2024-01-06 11:09:32
*/
@Service
public class FavoritesServiceImpl extends ServiceImpl<FavoritesMapper, Favorites>
    implements FavoritesService{


    @Resource
    private UsersService userService;

    @Override
    public void validFavorites(Favorites favorites, boolean add) {
        if (favorites == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Integer movieId = favorites.getMovieId();
        Integer id = favorites.getId();
        // 创建时，所有参数必须非空
        if (add) {
            //前面判斷string 後面判斷 Integer
            if (ObjectUtils.anyNull(movieId)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
        }
        if(id != null && id < 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
    }

    @Override
    public Page<Favorites> listPage(FavoritesQueryRequest favoritesQueryRequest) {
        Favorites favoritesQuery = new Favorites();
        BeanUtils.copyProperties(favoritesQueryRequest, favoritesQuery);
        long current = favoritesQueryRequest.getCurrent();
        long size = favoritesQueryRequest.getPageSize();
        String sortField = favoritesQueryRequest.getSortField();
        String sortOrder = favoritesQueryRequest.getSortOrder();
//        String content = favoritesQuery.getFavoritesName();
        // content 需支持模糊搜索
//        favoritesQuery.setFavoritesName(content);
        // 限制爬虫
        if (size > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<Favorites> queryWrapper = new QueryWrapper<>(favoritesQuery);
//        queryWrapper.like(StringUtils.isNotBlank(content), "favoritesName", content);
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        return this.page(new Page<>(current, size), queryWrapper);
    }

    @Override
    public List<Favorites> getListFavorites(Favorites favoritesQuery) {
        QueryWrapper<Favorites> queryWrapper = new QueryWrapper<>(favoritesQuery);
        return this.list(queryWrapper);
    }

    @Override
    public Favorites getFavoritesById(long id) {
        return this.getById(id);
    }

    @Override
    public boolean toUpdate(Favorites favorites) {
        long id = favorites.getId();
        // 判断是否存在
        Favorites oldFavorites = this.getById(id);
        if (oldFavorites == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return this.updateById(favorites);
    }

    @Override
    public boolean removeFavoritesById(long id) {
        return this.removeFavoritesById(id);
    }

    @Override
    public Integer toAddFavorites(Favorites favorites) {
        boolean result = this.save(favorites);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        return favorites.getId();
    }

}




