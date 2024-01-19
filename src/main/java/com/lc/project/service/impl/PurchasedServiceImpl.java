package com.lc.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lc.project.mapper.PurchasedMapper;
import com.lc.project.model.entity.Movie;
import com.lc.project.model.entity.Purchased;
import com.lc.project.model.entity.Users;
import com.lc.project.model.vo.PurchasedVO;
import com.lc.project.service.MovieService;
import com.lc.project.service.PurchasedService;
import com.lc.project.service.UsersService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
* @author asus
* @description 针对表【purchased(已购买电影表)】的数据库操作Service实现
* @createDate 2024-01-06 11:09:45
*/
@Service
public class PurchasedServiceImpl extends ServiceImpl<PurchasedMapper, Purchased>
    implements PurchasedService{

    @Resource
    private UsersService usersService;

    @Resource
    private MovieService movieService;

    @Override
    public List<PurchasedVO> getMyPurchased() {
        Users loginUser = usersService.getLoginUser();
        QueryWrapper<Purchased> purchasedQueryWrapper = new QueryWrapper<>();
        purchasedQueryWrapper.eq("userId",loginUser.getId());
        List<Purchased> list = this.list(purchasedQueryWrapper);
        ArrayList<PurchasedVO> purchasedVOS = new ArrayList<>();
        list.forEach(item->{
            PurchasedVO purchasedVO = new PurchasedVO();
            Integer movieId = item.getMovieId();
            Movie movie = movieService.getById(movieId);
            BeanUtils.copyProperties(item,purchasedVO);
            purchasedVO.setMovie(movie);
            purchasedVOS.add(purchasedVO);
        });
        return purchasedVOS;
    }
}




