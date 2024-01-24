package com.lc.project.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lc.project.annotation.AuthCheck;
import com.lc.project.common.BaseResponse;
import com.lc.project.common.DeleteRequest;
import com.lc.project.common.ErrorCode;
import com.lc.project.common.ResultUtils;
import com.lc.project.exception.BusinessException;
import com.lc.project.model.dto.favorites.FavoritesAddRequest;
import com.lc.project.model.dto.favorites.FavoritesQueryRequest;
import com.lc.project.model.dto.favorites.FavoritesUpdateRequest;
import com.lc.project.model.entity.Favorites;
import com.lc.project.model.entity.Users;
import com.lc.project.model.vo.FavoritesVo;
import com.lc.project.service.FavoritesService;
import com.lc.project.service.UsersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 電影
 *
 * @author Lc
 */
@RestController
@RequestMapping("/favorites")
@Slf4j
public class FavoritesController {

    @Resource
    private FavoritesService favoritesService;

    @Resource
    private UsersService userService;


    /**
     * 创建
     *
     * @param favoritesAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Integer> addFavorites(@RequestBody FavoritesAddRequest favoritesAddRequest, HttpServletRequest request) {
        if (favoritesAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Favorites favorites = new Favorites();
        BeanUtils.copyProperties(favoritesAddRequest, favorites);
        // 校验
        favoritesService.validFavorites(favorites, true);
        Users loginUser = userService.getLoginUser(request);
        favorites.setUserId(loginUser.getId());
        Integer newFavoritesId =  favoritesService.toAddFavorites(favorites);
        return ResultUtils.success(newFavoritesId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteFavorites(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = deleteRequest.getId();
        // 判断是否存在
        Favorites oldFavorites = favoritesService.getById(id);
        if (oldFavorites == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可删除
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = favoritesService.removeFavoritesById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新
     *
     * @param favoritesUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateFavorites(@RequestBody FavoritesUpdateRequest favoritesUpdateRequest,
                                            HttpServletRequest request) {
        if (favoritesUpdateRequest == null || favoritesUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //管理员可修改
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        Favorites favorites = new Favorites();
        BeanUtils.copyProperties(favoritesUpdateRequest, favorites);
        // 参数校验
        favoritesService.validFavorites(favorites, false);
        boolean result = favoritesService.toUpdate(favorites);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<Favorites> getFavoritesById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Favorites favorites = favoritesService.getFavoritesById(id);
        return ResultUtils.success(favorites);
    }

    /**
     * 获取列表（仅管理员可使用）
     *
     * @param favoritesQueryRequest
     * @return
     */
    @AuthCheck(mustRole = "admin")
    @GetMapping("/list")
    public BaseResponse<List<Favorites>> listFavorites(FavoritesQueryRequest favoritesQueryRequest) {
        Favorites favoritesQuery = new Favorites();
        if (favoritesQueryRequest != null) {
            BeanUtils.copyProperties(favoritesQueryRequest, favoritesQuery);
        }
        List<Favorites> favoritesList = favoritesService.getListFavorites(favoritesQuery);
        return ResultUtils.success(favoritesList);
    }

    /**
     * 分页获取列表
     *
     * @param favoritesQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/list/page")
    public BaseResponse<List<Favorites>> listFavoritesByPage(FavoritesQueryRequest favoritesQueryRequest, HttpServletRequest request) {
        if (favoritesQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
//        Page<FavoritesVo> favoritesPage = favoritesService.listPage(favoritesQueryRequest);
        Users loginUser = userService.getLoginUser();
        List<Favorites> favoritesList = favoritesService.getMyFavoritesByUserId(loginUser.getId());
        return ResultUtils.success(favoritesList);
    }
}
