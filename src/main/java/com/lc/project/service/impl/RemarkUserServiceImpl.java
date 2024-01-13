package com.lc.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lc.project.common.ErrorCode;
import com.lc.project.exception.BusinessException;
import com.lc.project.mapper.RemarkMapper;
import com.lc.project.model.dto.remark.RemarkUserAddQuery;
import com.lc.project.model.entity.Remark;
import com.lc.project.model.entity.RemarkUser;
import com.lc.project.model.entity.Users;
import com.lc.project.service.RemarkService;
import com.lc.project.service.RemarkUserService;
import com.lc.project.mapper.RemarkUserMapper;
import com.lc.project.service.UsersService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author asus
 * @description 针对表【remark_user(用户对评论是否支持)】的数据库操作Service实现
 * @createDate 2024-01-13 19:34:13
 */
@Service
public class RemarkUserServiceImpl extends ServiceImpl<RemarkUserMapper, RemarkUser>
        implements RemarkUserService {

    @Resource
    private RemarkUserMapper remarkUserMapper;

    @Resource
    private UsersService usersService;

    @Resource
    private RemarkService remarkService;

    @Override
    public boolean like(RemarkUserAddQuery remarkUserAddQuery) {
        Users loginUser = usersService.getLoginUser();
        Integer remarkId = remarkUserAddQuery.getRemarkId();
        Integer support = remarkUserAddQuery.getSupport();
        RemarkUser remarkUser = new RemarkUser();
        BeanUtils.copyProperties(remarkUserAddQuery, remarkUser);
        //设置用户信息
        remarkUser.setUserId(loginUser.getId());
        QueryWrapper<RemarkUser> remarkUserQueryWrapper = new QueryWrapper<>();
        remarkUserQueryWrapper.eq("userId", remarkUser.getUserId());
        remarkUserQueryWrapper.eq("remarkId", remarkId);
        RemarkUser oldRemarkUser = remarkUserMapper.selectOne(remarkUserQueryWrapper);
        //如果没有相关的就是第一次判断
        if (oldRemarkUser == null) {
            UpdateWrapper<Remark> remarkUpdateWrapper = new UpdateWrapper<>();
            remarkUpdateWrapper.eq("id",remarkId);
            if(support == 1){
                remarkUpdateWrapper.setSql("disLiked = disLiked + 1 ");
            }
            if(support == 2){
                remarkUpdateWrapper.setSql("liked = liked + 1");
            }
            remarkService.update(remarkUpdateWrapper);
            return this.save(remarkUser);
        }
        remarkUser.setId(oldRemarkUser.getId());
        //之前的support
        Integer oldSupport = oldRemarkUser.getSupport();
        if(remarkService.updateBySupport(oldSupport,support,remarkId)){
            remarkUser.setSupport(0);
        }
        return this.updateById(remarkUser);
    }


}




