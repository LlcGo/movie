package com.lc.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lc.project.mapper.BarrageMapper;
import com.lc.project.model.dto.barrage.BarrageAddRequest;
import com.lc.project.model.entity.Barrage;
import com.lc.project.model.entity.Users;
import com.lc.project.model.vo.BarrageVO;
import com.lc.project.service.BarrageService;
import com.lc.project.service.UsersService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author asus
* @description 针对表【barrage(弹幕表)】的数据库操作Service实现
* @createDate 2024-01-06 11:09:18
*/
@Service
public class BarrageServiceImpl extends ServiceImpl<BarrageMapper, Barrage>
    implements BarrageService{

    @Resource
    private UsersService usersService;

    @Override
    public Integer toAddBarrage(Barrage barrage) {
        this.save(barrage);
        return barrage.getId();
    }

    @Override
    public Barrage queryToBarrage(BarrageAddRequest barrageAddRequest){
        Barrage barrage = new Barrage();
        barrage.setMovieId(barrageAddRequest.getMovieId());
        barrage.setUserId(barrageAddRequest.getUserId());
        barrage.setContent(barrageAddRequest.getContent());
        barrage.setAppTime(barrageAddRequest.getAppTime());
        barrage.setColor(barrageAddRequest.getColor());
        return barrage;
    }

    public BarrageVO barrageToVo(Barrage barrage){
        BarrageVO barrageVO = new BarrageVO();
        barrageVO.setText(barrage.getContent());
        barrageVO.setId(barrage.getId());
        barrageVO.setTime(Double.valueOf(barrage.getAppTime()));
        barrageVO.setColor(barrage.getColor());
        barrageVO.setCreateTime(barrage.getCreateTime());
        barrageVO.setUserId(barrage.getUserId());
//        barrageVO.setForce(true);
        return barrageVO;
    }

    @Override
    public List<BarrageVO> getBarrageByMovieId(long movieId) {
        //根据电影Id获取所有的弹幕
        QueryWrapper<Barrage> barrageQueryWrapper = new QueryWrapper<>();
        barrageQueryWrapper.eq("movieId",movieId);
        barrageQueryWrapper.orderByDesc("appTime");
        List<Barrage> barrageList = this.list(barrageQueryWrapper);
        Users loginUser = usersService.getLoginUser();
        String currentUserId = loginUser.getId();
        ArrayList<BarrageVO> barrageVOArrayList = new ArrayList<>();
        barrageList.forEach(item -> {
            BarrageVO barrageVO = barrageToVo(item);
            if( item.getUserId().equals(currentUserId)){
                barrageVO.setIsMe(true);
            }
            barrageVOArrayList.add(barrageVO);
        });
        return barrageVOArrayList
                .stream()
                .sorted(Comparator.comparing(BarrageVO::getTime))
                .collect(Collectors.toList());
    }
}




