package com.lc.project.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lc.project.annotation.AuthCheck;
import com.lc.project.common.BaseResponse;
import com.lc.project.common.DeleteRequest;
import com.lc.project.common.ErrorCode;
import com.lc.project.common.ResultUtils;
import com.lc.project.exception.BusinessException;
import com.lc.project.model.dto.barrage.BarrageAddRequest;
import com.lc.project.model.dto.barrage.BarrageQueryRequest;
import com.lc.project.model.dto.barrage.BarrageUpdateRequest;
import com.lc.project.model.entity.Barrage;
import com.lc.project.model.entity.Users;
import com.lc.project.model.vo.BarrageVO;
import com.lc.project.service.BarrageService;
import com.lc.project.service.UsersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/barrage")
@Slf4j
public class BarrageController {
    
    @Resource
    private UsersService userService;

    @Resource
    private BarrageService barrageService;

    /**
     * 发送弹幕
     *
     * @param barrageAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Integer> addBarrage(@RequestBody BarrageAddRequest barrageAddRequest, HttpServletRequest request) {
        if (barrageAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Barrage barrage = barrageService.queryToBarrage(barrageAddRequest);
        Integer movieId = barrage.getMovieId();
        if(barrage.getContent() == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if(movieId < 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Users loginUser = userService.getLoginUser(request);
        barrage.setUserId(loginUser.getId());
        Integer newBarrageId = barrageService.toAddBarrage(barrage);
        return ResultUtils.success(newBarrageId);
    }



    /**
     * 根据 movieId 获取
     *
     * @param movieId
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<List<BarrageVO>> getBarrageById(long movieId) {
        if (movieId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<BarrageVO> barrage = barrageService.getBarrageByMovieId(movieId);
        return ResultUtils.success(barrage);
    }


    @PostMapping("/delete")
    public BaseResponse<Boolean> delete(Barrage barrage){
        if(barrage.getId() < 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userId = barrage.getUserId();
        Users loginUser = userService.getLoginUser();
        if(!userId.equals(loginUser.getId())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"不能删除别人的弹幕");
        }
        boolean b = barrageService.removeById(barrage);
        return ResultUtils.success(b);
    }

    


   
}
