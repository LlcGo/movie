package com.lc.project.controller;
import java.util.Date;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lc.project.annotation.AuthCheck;
import com.lc.project.common.BaseResponse;
import com.lc.project.common.DeleteRequest;
import com.lc.project.common.ErrorCode;
import com.lc.project.common.ResultUtils;
import com.lc.project.exception.BusinessException;
import com.lc.project.mapper.MovieMapper;
import com.lc.project.model.dto.remark.RemarkAddRequest;
import com.lc.project.model.dto.remark.RemarkDeleteRequest;
import com.lc.project.model.dto.remark.RemarkQueryRequest;
import com.lc.project.model.dto.remark.RemarkUpdateRequest;
import com.lc.project.model.entity.Movie;
import com.lc.project.model.entity.Remark;
import com.lc.project.model.entity.Users;
import com.lc.project.model.vo.RemarkVo;
import com.lc.project.service.MovieService;
import com.lc.project.service.RemarkService;
import com.lc.project.service.UsersService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/remark")
public class RemarkController {

    @Resource
    private RemarkService remarkService;


    @Resource
    private UsersService userService;

    @Resource
    private MovieMapper movieMapper;
    
    /**
     * 创建
     *
     * @param remarkAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Integer> addRemark(@RequestBody RemarkAddRequest remarkAddRequest, HttpServletRequest request) {
        if (remarkAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Remark remark = new Remark();

        BeanUtils.copyProperties(remarkAddRequest, remark);
        // 校验
        Integer movieId = remark.getMovieId();
        if (movieId < 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Users loginUser = userService.getLoginUser(request);
        remark.setUserId(loginUser.getId());
        Integer newRemarkId = remarkService.toAddRemark(remark);
        return ResultUtils.success(newRemarkId);
    }

    /**
     * 管理员删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteRemark(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = deleteRequest.getId();
        // 判断是否存在
        Remark oldRemark = remarkService.getById(id);
        if (oldRemark == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅管理员可删除
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = remarkService.removeRemarkById(id);
        return ResultUtils.success(b);
    }


    /**
     * 用户删除
     * @param remarkDeleteRequest
     * @param request
     * @return
     */
    @PostMapping("/user/delete")
    public BaseResponse<Boolean> deleteUserRemark(@RequestBody RemarkDeleteRequest remarkDeleteRequest, HttpServletRequest request) {
        if (remarkDeleteRequest == null || remarkDeleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = remarkDeleteRequest.getId();
        // 判断是否存在
        Remark oldRemark = remarkService.getById(id);
        if (oldRemark == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        Users loginUser = userService.getLoginUser();
        //哪个用户的评论
        String userId = remarkDeleteRequest.getUserId();
        // 仅本人可删除
        if (!loginUser.getId().equals(userId)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = remarkService.removeRemarkById(remarkDeleteRequest);
        return ResultUtils.success(b);
    }


//    /**
//     * 更新
//     *
//     * @param remarkUpdateRequest
//     * @param request
//     * @return
//     */
//    @PostMapping("/update")
//    public BaseResponse<Boolean> updateRemark(@RequestBody RemarkUpdateRequest remarkUpdateRequest,
//                                             HttpServletRequest request) {
//        if (remarkUpdateRequest == null || remarkUpdateRequest.getId() <= 0) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        //管理员可修改
//        if (!userService.isAdmin(request)) {
//            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
//        }
//        Remark remark = new Remark();
//        BeanUtils.copyProperties(remarkUpdateRequest, remark);
//        // 参数校验
////        remarkService.validRemark(remark, false);
//        boolean result = remarkService.toUpdate(remark);
//        return ResultUtils.success(result);
//    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<Remark> getRemarkById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Remark remark = remarkService.getRemarkById(id);
        return ResultUtils.success(remark);
    }

    /**
     * 获取列表（仅管理员可使用）
     *
     * @param remarkQueryRequest
     * @return
     */
    @AuthCheck(mustRole = "admin")
    @GetMapping("/list")
    public BaseResponse<List<Remark>> listRemark(RemarkQueryRequest remarkQueryRequest) {

        List<Remark> remarkList = remarkService.getListRemark(remarkQueryRequest);
        return ResultUtils.success(remarkList);
    }

    /**
     * 分页获取列表
     *
     * @param remarkQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/list/page")
    public BaseResponse<List<Remark>> listRemarkByPage(RemarkQueryRequest remarkQueryRequest, HttpServletRequest request) {
        if (remarkQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<Remark> remarkPage = remarkService.listPage(remarkQueryRequest);
        return ResultUtils.success(remarkPage);
    }

    @GetMapping("/list/count")
    public BaseResponse<Long> listCount(RemarkQueryRequest remarkQueryRequest, HttpServletRequest request) {
        if (remarkQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long total = remarkService.listCount(remarkQueryRequest);
        return ResultUtils.success(total);
    }

    @PostMapping("/admin/delete")
    public BaseResponse<Boolean> adminDelete(Integer id) {
        if (id == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Remark remark = new Remark();
        remark.setId(id);
        remark.setState(1);
        return ResultUtils.success(remarkService.updateById(remark));
    }

    @PostMapping("/admin/hf")
    public BaseResponse<Boolean> adminHf(Integer id) {
        if (id == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Remark remark = new Remark();
        remark.setId(id);
        remark.setState(0);
        return ResultUtils.success(remarkService.updateById(remark));
    }

    /**
     * 评论是否通过
     */
    @PostMapping("/sh")
    public BaseResponse<Integer> shRemark(Integer id) {
        if (id == null || id < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Remark remark = remarkService.getById(id);
        if(remark == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Integer newRemarkId = remarkService.shRemark(remark);
        return ResultUtils.success(newRemarkId);
    }

    @PostMapping("/admin/add")
    public BaseResponse<Boolean> adminAdd(String movieName,String userId,String content) {
        if (StrUtil.isBlank(movieName)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请输入电影名");
        }
        if (StrUtil.isBlank(userId)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请重新登录");
        }
        if (StrUtil.isBlank(content)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请输入内容");
        }
        QueryWrapper<Movie> movieQueryWrapper = new QueryWrapper<>();
        movieQueryWrapper.eq("movieName",movieName);
        Movie movie = movieMapper.selectOne(movieQueryWrapper);
        if(movie == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"没有该电影,请重新输入");
        }
        Remark remark = new Remark();
        remark.setMovieId(movie.getId());
        remark.setContent(content);
        remark.setUserId(userId);
        return ResultUtils.success(remarkService.save(remark));
    }
}
