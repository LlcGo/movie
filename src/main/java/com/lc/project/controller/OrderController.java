package com.lc.project.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lc.project.annotation.AuthCheck;
import com.lc.project.common.BaseResponse;
import com.lc.project.common.DeleteRequest;
import com.lc.project.common.ErrorCode;
import com.lc.project.common.ResultUtils;
import com.lc.project.constant.CommonConstant;
import com.lc.project.exception.BusinessException;
import com.lc.project.model.dto.order.OrderAddRequest;
import com.lc.project.model.dto.order.OrderByRequest;
import com.lc.project.model.dto.order.OrderQueryRequest;
import com.lc.project.model.dto.order.OrderUpdateRequest;
import com.lc.project.model.entity.Order;
import com.lc.project.model.entity.Users;
import com.lc.project.model.enums.OrderDayEnum;
import com.lc.project.model.vo.OrderVO;
import com.lc.project.rabbitmq.RabbitMQUtils;
import com.lc.project.service.OrderService;
import com.lc.project.service.UsersService;
import com.lc.project.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
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
@RequestMapping("/order")
@Slf4j
public class OrderController {

    @Resource
    private OrderService orderService;

    @Resource
    private UsersService userService;

    @Value("${rabbit.ttl}")
    private Integer rabbitTTL;

    @Resource
    private RedisUtils redisUtils;

    /**
     * 创建
     *
     * @param orderAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Integer> addOrder(@RequestBody OrderAddRequest orderAddRequest, HttpServletRequest request) {
        if (orderAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Order order = new Order();
        String vipType = orderAddRequest.getVipType();
        order.setVipType(OrderDayEnum.getValueByText(vipType));
        BeanUtils.copyProperties(orderAddRequest, order);
        // 校验
        Integer state = order.getState();
        orderService.validOrder(order, state != null && state == 1);
        Users loginUser = userService.getLoginUser(request);
        order.setUserId(loginUser.getId());
        Integer newOrderId = orderService.toAddOrder(order);
        return ResultUtils.success(newOrderId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteOrder(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = deleteRequest.getId();
        // 判断是否存在
        Order oldOrder = orderService.getById(id);
        if (oldOrder == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可删除
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = orderService.removeOrderById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新
     *
     * @param orderUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateOrder(@RequestBody OrderUpdateRequest orderUpdateRequest,
                                             HttpServletRequest request) {
        if (orderUpdateRequest == null || orderUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //管理员可修改
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        Order order = new Order();
        BeanUtils.copyProperties(orderUpdateRequest, order);
        // 参数校验
//        orderService.validOrder(order, false);
        boolean result = orderService.toUpdate(order);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<Order> getOrderById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Order order = orderService.getOrderById(id);
        return ResultUtils.success(order);
    }

    @GetMapping("/get/myOrder")
    public BaseResponse<List<Order>> getOrderByUserId() {
        List<Order> order = orderService.getOrderByUserId();
        return ResultUtils.success(order);
    }


    /**
     * 获取列表（仅管理员可使用）
     *
     * @param orderQueryRequest
     * @return
     */
    @AuthCheck(mustRole = "admin")
    @GetMapping("/list")
    public BaseResponse<List<Order>> listOrder(OrderQueryRequest orderQueryRequest) {
        Order orderQuery = new Order();
        if (orderQueryRequest != null) {
            BeanUtils.copyProperties(orderQueryRequest, orderQuery);
        }
        List<Order> orderList = orderService.getListOrder(orderQuery);
        return ResultUtils.success(orderList);
    }

    /**
     * 分页获取列表
     *
     * @param orderQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<Order>> listOrderByPage(OrderQueryRequest orderQueryRequest, HttpServletRequest request) {
        if (orderQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Page<Order> orderPage = orderService.listPage(orderQueryRequest);
        return ResultUtils.success(orderPage);
    }


    /**
     * 订单中的下单
     *
     * @param
     * @param request
     * @return
     */
    @PostMapping("/buy")
    public BaseResponse<Boolean> OrderBuy(@RequestBody OrderByRequest orderByRequest, HttpServletRequest request) {
        if (orderByRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Users loginUser = userService.getLoginUser(request);
        orderByRequest.setUserId(loginUser.getId());
        Boolean flag = orderService.orderBuy(orderByRequest);
        return ResultUtils.success(flag);
    }

    @PostMapping("/tobuy")
    public BaseResponse<Boolean> toBuy(@RequestBody OrderByRequest orderByRequest, HttpServletRequest request) {
        if (orderByRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Users loginUser = userService.getLoginUser(request);
        orderByRequest.setUserId(loginUser.getId());
        Boolean flag = orderService.toBuy(orderByRequest);
        return ResultUtils.success(flag);
    }

    @PostMapping("/getDDLTime")
    public BaseResponse<Integer> getDDLTime() {
        Integer ddlTime = null;
        Object time = redisUtils.get(CommonConstant.ORDER_DDL_TIME);
        if(time != null){
            ddlTime = (Integer) time;
        }else {
            ddlTime = rabbitTTL;
        }
        return ResultUtils.success(ddlTime);
    }
}
