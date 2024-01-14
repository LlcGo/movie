package com.lc.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lc.project.model.dto.barrage.BarrageAddRequest;
import com.lc.project.model.entity.Barrage;
import com.lc.project.model.vo.BarrageVO;

import java.util.List;

/**
* @author asus
* @description 针对表【barrage(弹幕表)】的数据库操作Service
* @createDate 2024-01-06 11:09:18
*/
public interface BarrageService extends IService<Barrage> {

    Integer toAddBarrage(Barrage barrage);


    Barrage queryToBarrage(BarrageAddRequest barrageAddRequest);

    List<BarrageVO> getBarrageByMovieId(long movieId);
}
