package com.lc.project.service.impl;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lc.project.common.ErrorCode;
import com.lc.project.constant.CommonConstant;
import com.lc.project.exception.BusinessException;
import com.lc.project.mapper.OrderMapper;
import com.lc.project.mapper.VipMapper;
import com.lc.project.model.dto.order.OrderByRequest;
import com.lc.project.model.dto.order.OrderQueryRequest;
import com.lc.project.model.entity.*;
import com.lc.project.model.enums.OrderDayEnum;
import com.lc.project.model.vo.OrderVO;
import com.lc.project.rabbitmq.RabbitMQUtils;
import com.lc.project.service.*;
import com.lc.project.utils.DayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author asus
 * @description 针对表【order(订单表)】的数据库操作Service实现
 * @createDate 2024-01-06 11:09:42
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order>
        implements OrderService {

    @Resource
    private MovieService movieService;

    @Resource
    private RabbitMQUtils rabbitMQUtils;

    @Resource
    private PurchasedService purchasedService;

    @Resource
    private VipService vipService;

    @Resource
    private DayUtils dayUtils;

    @Resource
    private VipMapper vipMapper;

    @Resource
    private UsersService usersService;
    @Override
    public Page<Order> listPage(OrderQueryRequest orderQueryRequest) {
        Order orderQuery = new Order();
        BeanUtils.copyProperties(orderQueryRequest, orderQuery);
        long current = orderQueryRequest.getCurrent();
        long size = orderQueryRequest.getPageSize();
        String sortField = orderQueryRequest.getSortField();
        String sortOrder = orderQueryRequest.getSortOrder();
//        String content = orderQuery.getFavoritesName();
        // content 需支持模糊搜索
//        orderQuery.setFavoritesName(content);
        // 限制爬虫
        if (size > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>(orderQuery);
//        queryWrapper.like(StringUtils.isNotBlank(content), "orderName", content);
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        return this.page(new Page<>(current, size), queryWrapper);
    }

    @Override
    public List<Order> getListOrder(Order orderQuery) {
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>(orderQuery);
        return this.list(queryWrapper);
    }

    @Override
    public Order getOrderById(long id) {
        return this.getById(id);
    }

    @Override
    public boolean toUpdate(Order order) {
        long id = order.getId();
        // 判断是否存在
        Order oldOrder = this.getById(id);
        if (oldOrder == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return this.updateById(order);
    }

    @Override
    public void validOrder(Order order, boolean addMovie) {
        Integer state = order.getState();
        Integer movieId = order.getMovieId();
        if (addMovie) {
            if (movieId == null || movieId < 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
        }
        if (state == null || state < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
    }

    @Override
    public boolean removeOrderById(long id) {
        return this.removeById(id);
    }

    @Override
    public Integer toAddOrder(Order order) {
        String userId = order.getUserId();
        Integer movieId = order.getMovieId();
        //查看是vip下单 0 还是 电影下单 1
        Integer state = order.getState();
        //查看是否有这个订单业务如果有那就不重新插入 如果是vip订单 
        //用户id 订单状态 电影id 是否删除
        //如果是要开会员 那可以重复下订单，重复下的订单就是续费
        if (state == 0) {
//          直接插入订单标志 未完成
            this.save(order);
            //做延迟队列处理
            rabbitMQUtils.sendMessage(order.getId().toString());
            return order.getId();
        }
        Movie movie = movieService.getById(movieId);
        if (movie == null) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "不在该电影");
        }
        //如果是电影订单
        //查看这个订单关系是否已经存在如果已经存在不重新插入 如果是电影下单，也直接插入订单标识 未完成,
        QueryWrapper<Order> orderQueryWrapper = new QueryWrapper<>();
        orderQueryWrapper.eq("userId", userId);
        orderQueryWrapper.eq("state", state);
        orderQueryWrapper.eq("orderState", 0);
        orderQueryWrapper.eq("movieId", movieId);
        long count = this.count(orderQueryWrapper);
        if (count > 0) {
            //前端跳转订单页面
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "订单已存在");
        }
        this.save(order);
        //做延迟队列处理
        rabbitMQUtils.sendMessage(order.getId().toString());
        return order.getId();
    }

    @Override
    public Boolean orderBuy(OrderByRequest orderByRequest) {
        Integer orderId = orderByRequest.getOrderId();
        Integer state = orderByRequest.getState();
        String userId = orderByRequest.getUserId();
        Integer movieId = orderByRequest.getMovieId();
        String date = orderByRequest.getDate();
        if(orderId < 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (state < 0 || state > 2) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        //电影下单：//state 1
        if (state == 1) {
            //电影已购买不可以再下单购买
            QueryWrapper<Order> orderQueryWrapper = new QueryWrapper<>();
            orderQueryWrapper.eq("userId", userId);
            orderQueryWrapper.eq("id",orderId);
            orderQueryWrapper.eq("movieId", movieId);
            orderQueryWrapper.eq("orderState",1);
            long count = this.count(orderQueryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "您已购买");
            }
            //修改订单为已支付,
            Order order = this.getById(orderId);
            order.setOrderState(1);
            this.updateById(order);
            //添加电影与人的关系表数据
            Purchased purchased = new Purchased();
            purchased.setUserId(userId);
            purchased.setMovieId(movieId);
            return purchasedService.save(purchased);
        }

        //state 0 //开会员：//是否第一次开通 （查询vip数据库是否有数据）
        QueryWrapper<Vip> vipQueryWrapper = new QueryWrapper<>();
        vipQueryWrapper.eq("userId",userId);
        Vip oldVip = vipMapper.selectOne(vipQueryWrapper);
        if(oldVip == null){
            //第一次开通 生成订单并且设置为已下单 往vip表里插入数据
            Order order = this.getById(orderId);
            order.setOrderState(1);
            this.updateById(order);
            Vip vip = new Vip();
            vip.setUserId(userId);
            //开通多少天
            if(date.equals(OrderDayEnum.YEAR.getText())){
                  //年多少天
                int currentYear = dayUtils.getCurrentYear();
                SetVipOverTime(currentYear,vip);
            }
            if(date.equals(OrderDayEnum.MONTHS.getText())){
                 //月多少天
                int currentMonth = dayUtils.getCurrentMonth();
                SetVipOverTime(currentMonth,vip);
            }
            if(date.equals(OrderDayEnum.QUARTER.getText())){
                //季度多少天
                int currentQuarter = dayUtils.getCurrentQuarter();
                SetVipOverTime(currentQuarter,vip);
            }
            return vipService.save(vip);
        }
        //如果是续费 则插入新的订单并且设置已下单，
        Order order = this.getById(orderId);
        order.setOrderState(1);
        this.updateById(order);
        //下单vip续费 修改vip数据表内的到期时间
        Date overTime = oldVip.getOverTime();
        //转换时间
        Instant instant = overTime.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();

        LocalDateTime localOverTime = instant.atZone(zoneId).toLocalDateTime();
        //续费多少天 //获取数据库时间，将其加上开会员的时间,最后修改Vip数据库增加时长
        if(date.equals(OrderDayEnum.YEAR.getText())){
            //年多少天
            int currentYear = dayUtils.getCurrentYear();
            setVipLayTime(localOverTime,currentYear,zoneId,oldVip);
        }
        if(date.equals(OrderDayEnum.MONTHS.getText())){
            //月多少天
            int currentMonth = dayUtils.getCurrentMonth();
            setVipLayTime(localOverTime,currentMonth,zoneId,oldVip);
        }
        if(date.equals(OrderDayEnum.QUARTER.getText())){
            //季度多少天
            int currentQuarter = dayUtils.getCurrentQuarter();
            setVipLayTime(localOverTime,currentQuarter,zoneId,oldVip);
        }
       return vipService.updateById(oldVip);
    }

    @Override
    public List<OrderVO> getOrderByUserId() {
        Users loginUser = usersService.getLoginUser();
        String id = loginUser.getId();
        QueryWrapper<Order> orderQueryWrapper = new QueryWrapper<>();
        orderQueryWrapper.eq("userId",id);
        List<Order> list = this.list(orderQueryWrapper);
        ArrayList<OrderVO> orderVOS = new ArrayList<>();
        list.forEach(item->{
            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(item,orderVO);
            Integer movieId = item.getMovieId();
            Movie movie = movieService.getById(movieId);
            orderVO.setMovie(movie);
            orderVOS.add(orderVO);
        });
        return orderVOS;
    }


    public void SetVipOverTime(int day,Vip vip){
        LocalDateTime now = LocalDateTimeUtil.now();
        LocalDateTime localDateTime = LocalDateTimeUtil.parse(now.toString());
        LocalDateTime offset = LocalDateTimeUtil.offset(localDateTime, day, ChronoUnit.DAYS);
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = offset.atZone(zoneId);
        Date OverTime = Date.from(zdt.toInstant());
        vip.setOverTime(OverTime);
    }

    public void setVipLayTime(LocalDateTime localOverTime,int day,ZoneId zoneId,Vip oldVip){
        LocalDateTime offset = LocalDateTimeUtil.offset(localOverTime, day, ChronoUnit.DAYS);
        ZonedDateTime zdt = offset.atZone(zoneId);
        Date OverTime = Date.from(zdt.toInstant());
        oldVip.setOverTime(OverTime);
    }


}




