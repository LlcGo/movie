package com.lc.project.service.impl;
import java.util.Date;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lc.project.common.ErrorCode;
import com.lc.project.constant.CommonConstant;
import com.lc.project.exception.BusinessException;
import com.lc.project.mapper.FavoritesMapper;
import com.lc.project.model.dto.favorites.FavoritesQueryRequest;
import com.lc.project.model.entity.Favorites;
import com.lc.project.model.entity.Movie;
import com.lc.project.model.entity.Users;
import com.lc.project.model.vo.FavoritesVo;
import com.lc.project.service.FavoritesService;
import com.lc.project.service.MovieService;
import com.lc.project.service.UsersService;
import com.lc.project.utils.RedisUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

import static com.lc.project.constant.CommonConstant.REDIS_FA_MOVIE;

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

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private MovieService movieService;

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
    public Page<FavoritesVo> listPage(FavoritesQueryRequest favoritesQueryRequest) {
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
        QueryWrapper<Favorites> queryWrapper = new QueryWrapper<>();
        Users loginUser = userService.getLoginUser();
        queryWrapper.eq("userId",loginUser.getId());
//        queryWrapper.like(StringUtils.isNotBlank(content), "favoritesName", content);
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        Page<Favorites> page = this.page(new Page<>(current, 50), queryWrapper);
        //获得每一个查询出来的favor给相关的影视复制
        List<Favorites> oldFavoritesList = page.getRecords();
        ArrayList<FavoritesVo> favoritesVos = new ArrayList<>();
        oldFavoritesList.forEach(favorites -> {
            FavoritesVo favoritesVo = new FavoritesVo();
            Movie movie = movieService.getById(favorites.getMovieId());
            BeanUtils.copyProperties(favorites,favoritesVo);
            favoritesVo.setMovie(movie);
            favoritesVos.add(favoritesVo);
        });
        Page<FavoritesVo> favoritesVoPage = new Page<>();
        BeanUtils.copyProperties(page,favoritesVoPage);
        favoritesVoPage.setRecords(favoritesVos);
        return favoritesVoPage;
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
        return this.removeById(id);
    }

    @Override
    public Integer toAddFavorites(Favorites favorites) {
        Integer movieId = favorites.getMovieId();
        String userId = favorites.getUserId();
        //如果已经收藏就
        if(redisUtils.sHasKey(REDIS_FA_MOVIE + movieId,userId)){
            redisUtils.setRemove(REDIS_FA_MOVIE + movieId ,userId);
            QueryWrapper<Favorites> favoritesQueryWrapper = new QueryWrapper<>();
            favoritesQueryWrapper.eq("userId",userId);
            favoritesQueryWrapper.eq("movieId",movieId);
            this.remove(favoritesQueryWrapper);
            //-1 代表已经取消收藏
            return -1;
        }
        //没有收藏就添加收藏
        boolean result = this.save(favorites);
        redisUtils.sSet(REDIS_FA_MOVIE + movieId ,userId);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        return favorites.getId();
    }

}




