package com.lc.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lc.project.model.entity.Purchased;
import com.lc.project.model.vo.PurchasedVO;

import java.util.List;

/**
* @author asus
* @description 针对表【purchased(已购买电影表)】的数据库操作Service
* @createDate 2024-01-06 11:09:45
*/
public interface PurchasedService extends IService<Purchased> {

    List<PurchasedVO> getMyPurchased();

}
