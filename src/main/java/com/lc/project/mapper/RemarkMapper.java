package com.lc.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lc.project.model.entity.Remark;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author asus
* @description 针对表【remark(评论表)】的数据库操作Mapper
* @createDate 2024-01-06 11:09:49
* @Entity com.lc.project.model.entity.Remark
*/
public interface RemarkMapper extends BaseMapper<Remark> {

    List<Remark> getRemarkAndUserPage(int movieId, long current, long pageSize);

    Integer getCountByMovieId(Integer movieId);

    List<Remark> listPageByUserAndMovie(@Param("current") long current,
                                        @Param("pageSize")long pageSize,
                                        @Param("nickName")String nickName,
                                        @Param("movieName")String movieName, String content);


    Integer countUserAndMovie(@Param("current") long current,
                              @Param("pageSize")long pageSize,
                              @Param("nickName")String nickName,
                              @Param("movieName")String movieName, String content);
}




