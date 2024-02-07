package com.lc.project.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lc.project.annotation.AuthCheck;
import com.lc.project.common.BaseResponse;
import com.lc.project.common.DeleteRequest;
import com.lc.project.common.ErrorCode;
import com.lc.project.common.ResultUtils;
import com.lc.project.exception.BusinessException;
import com.lc.project.model.dto.order.OrderAddRequest;
import com.lc.project.model.dto.order.OrderByRequest;
import com.lc.project.model.dto.order.OrderQueryRequest;
import com.lc.project.model.dto.order.OrderUpdateRequest;
import com.lc.project.model.entity.Order;
import com.lc.project.model.entity.Purchased;
import com.lc.project.model.entity.Users;
import com.lc.project.model.vo.OrderVO;
import com.lc.project.model.vo.PurchasedVO;
import com.lc.project.service.OrderService;
import com.lc.project.service.PurchasedService;
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
@RequestMapping("/purchased")
@Slf4j
public class PurchasedController {

    @Resource
    private PurchasedService purchasedService;




    @PostMapping("/get")
    public BaseResponse<List<Purchased>> getMyPurchased() {
        List<Purchased> list = purchasedService.getMyPurchased();
        return ResultUtils.success(list);
    }



}
