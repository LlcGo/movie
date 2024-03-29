package com.lc.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lc.project.common.ErrorCode;
import com.lc.project.exception.BusinessException;
import com.lc.project.mapper.RemarkMapper;
import com.lc.project.mapper.RemarkUserMapper;
import com.lc.project.model.dto.remark.RemarkDeleteRequest;
import com.lc.project.model.dto.remark.RemarkQueryRequest;
import com.lc.project.model.entity.Remark;
import com.lc.project.model.entity.RemarkUser;
import com.lc.project.model.entity.Users;
import com.lc.project.service.RemarkService;
import com.lc.project.service.RemarkUserService;
import com.lc.project.service.UsersService;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author asus
 * @description 针对表【remark(评论表)】的数据库操作Service实现
 * @createDate 2024-01-06 11:09:49
 */
@Service
public class RemarkServiceImpl extends ServiceImpl<RemarkMapper, Remark>
        implements RemarkService {



    @Resource
    private UsersService usersService;


    @Resource
    private RemarkMapper remarkMapper;

    @Resource
    private RemarkUserMapper remarkUserMapper;

    @Resource
    @Lazy
    private RemarkUserService remarkUserService;

    @Override
    public Integer toAddRemark(Remark remark) {
        String content = remark.getContent();
        Integer score = remark.getScore();
        String userId = remark.getUserId();
        Integer movieId = remark.getMovieId();
        QueryWrapper<Remark> remarkQueryWrapper = new QueryWrapper<>();
        remarkQueryWrapper.eq("userId", userId);
        remarkQueryWrapper.eq("movieId", movieId);
        List<Remark> list = this.list(remarkQueryWrapper);
        //如果有分数就是只是评分
        if (score != null) {
            //如果已经评分过了就是修改评分 并且只对一开始的评论修改评分
            if (list.size() > 0) {
                List<Remark> collect = list.stream().filter(item -> {
                    return item.getContent()== null;
                }).collect(Collectors.toList());
                System.out.println(collect);
                if (collect.size() == 0) {
                    //新增一个没有内容的数据 只是用来展示评分
                    this.save(remark);
                    return remark.getId();
                }
                //如果有的话 且只有一个 修改评分
                remark.setId(collect.get(0).getId());
                this.updateById(remark);
                return remark.getId();
            }
            //第一次评分新增
            this.save(remark);
            return remark.getId();
        }
        //如果有内容是评论 一直新增
        if (content.length() <= 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "评论内容不能为空");
        }
        this.save(remark);
        return remark.getId();
    }


    @Override
    public boolean removeRemarkById(long id) {
        //根据用户id,评论id,电影id删除评论
        return this.removeById(id);
    }

    @Override
    public boolean removeRemarkById(RemarkDeleteRequest remarkDeleteRequest) {
        Integer id = remarkDeleteRequest.getId();
        QueryWrapper<RemarkUser> remarkUserQueryWrapper = new QueryWrapper<>();
        remarkUserQueryWrapper.eq("remarkId",id);
        remarkUserService.remove(remarkUserQueryWrapper);
        return this.removeById(id);
    }

    @Override
    public Remark getRemarkById(long id) {
        return null;
    }

    @Override
    public List<Remark> getListRemark(RemarkQueryRequest remarkQuery) {
        long current = remarkQuery.getCurrent();
        long pageSize = remarkQuery.getPageSize();
        current = (current - 1)* pageSize;
        String nickName = remarkQuery.getNickName();
        String movieName = remarkQuery.getMovieName();
        String content = remarkQuery.getContent();
        List<Remark> remarkList = remarkMapper.listPageByUserAndMovie(current,pageSize,nickName,movieName,content);
        Integer total = remarkMapper.countUserAndMovie(current,pageSize,nickName,movieName,content);
        if(remarkList.size() > 0){
            remarkList.get(0).setTotal(total);
        }
        return remarkList;
    }

    @Override
    public List<Remark> listPage(RemarkQueryRequest remarkQueryRequest) {
        Integer movieId = remarkQueryRequest.getMovieId();
        long current = remarkQueryRequest.getCurrent();
        long pageSize = remarkQueryRequest.getPageSize();

        current = (current - 1)* pageSize;
        List<Remark> remarkList = remarkMapper.getRemarkAndUserPage(movieId,current,pageSize);


        Users loginUser = usersService.getLoginUser();
        if(loginUser == null){
            Integer total = remarkMapper.getCountByMovieId(movieId);
            if(remarkList.size() > 0){
                remarkList.get(0).setTotal(total);
            }
            return remarkList;
        }

        remarkList.forEach(item->{
            QueryWrapper<RemarkUser> remarkUserQueryWrapper = new QueryWrapper<>();
            remarkUserQueryWrapper.eq("userId",loginUser.getId());
            //当前评论id
            Integer currentRemarkId = item.getId();
            //当前用户id
            remarkUserQueryWrapper.eq("remarkId",currentRemarkId);
            RemarkUser remarkUser = remarkUserMapper.selectOne(remarkUserQueryWrapper);
            //如果没有记录就是什么都没有 全部 false
            if(remarkUser == null || remarkUser.getSupport() == 0){
                item.setLike(false);
                item.setHate(false);
            }else if(remarkUser.getSupport() == 1){
                item.setLike(false);
                item.setHate(true);
            }else {
                item.setLike(true);
                item.setHate(false);
            }
        });

        Integer total = remarkMapper.getCountByMovieId(movieId);
        if(remarkList.size() > 0){
            remarkList.get(0).setTotal(total);
        }
        return remarkList;
    }

    @Override
    public Long listCount(RemarkQueryRequest remarkQueryRequest) {
        Integer movieId = remarkQueryRequest.getMovieId();
        if(movieId == null || movieId < 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<Remark> remarkQueryWrapper = new QueryWrapper<>();
        remarkQueryWrapper.eq("movieId",movieId);
        remarkQueryWrapper.isNotNull("content");
        return this.count(remarkQueryWrapper);
    }


    /**
     * 根据之前的supprot 加减 现在的 支持 与 不支持数量
     *
     * @param oldSupport
     */
    @Override
    public boolean updateBySupport(Integer oldSupport, Integer nowSupport, Integer remarkId) {
        UpdateWrapper<Remark> remarkUpdateWrapper = new UpdateWrapper<>();
        remarkUpdateWrapper.eq("id",remarkId);
        switch (oldSupport) {
            case 0 :
                if(nowSupport == 1){
                    //如果之前是 0 现在 是 1 不支持 在不支持 + 1 ok
                    remarkUpdateWrapper.setSql("disLiked = disLiked + 1");
                    this.update(remarkUpdateWrapper);
                    return false;
                }
                if(nowSupport == 2){
                    //如果之前是 0 现在 是 2 支持  +  1 ok
                    remarkUpdateWrapper.setSql("liked = liked + 1");
                    this.update(remarkUpdateWrapper);
                    return false;
                }
            case 1 :
                if(nowSupport == 1){
                    //如果之前是 1 现在是 1 设置为 0 并且 不支持 - 1 ok
                    remarkUpdateWrapper.setSql("disLiked = disLiked - 1");
                    this.update(remarkUpdateWrapper);
                    return true;
                }
                if(nowSupport == 2){
                    //如果之前是 1 现在是 2 不支持 -1 支持 + 1 ok
                    remarkUpdateWrapper.setSql("liked = liked + 1");
                    remarkUpdateWrapper.setSql("disLiked = disLiked - 1");
                    this.update(remarkUpdateWrapper);
                    return false;
                }
            case 2 :
                if(nowSupport == 2){
                    //如果之前是 2 现在是 2 设置为 0 并且 支持 - 1 ok
                    remarkUpdateWrapper.setSql("liked = liked - 1");
                    this.update(remarkUpdateWrapper);
                    return true;
                }
                if(nowSupport == 1){
                    //如过之前是 2 现在是 1 支持 - 1 不支持 + 1 ok
                    remarkUpdateWrapper.setSql("disLiked = disLiked + 1");
                    remarkUpdateWrapper.setSql("liked = liked - 1");
                    this.update(remarkUpdateWrapper);
                    return false;
                }
        }
        throw new BusinessException(ErrorCode.SYSTEM_ERROR);
    }

    @Override
    public Integer shRemark(Remark remark) {
        Integer id = remark.getId();
        Integer state = remark.getState();
        if(state.equals(0)){
            remark.setState(1);
        }else {
            remark.setState(0);
        }
        this.updateById(remark);
        return id;
    }

}




