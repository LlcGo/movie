package com.lc.project.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lc.project.model.dto.remark.RemarkDeleteRequest;
import com.lc.project.model.dto.remark.RemarkQueryRequest;
import com.lc.project.model.entity.Remark;
import com.lc.project.model.vo.RemarkVo;

import java.util.List;

/**
* @author asus
* @description 针对表【remark(评论表)】的数据库操作Service
* @createDate 2024-01-06 11:09:49
*/
public interface RemarkService extends IService<Remark> {

    Integer toAddRemark(Remark remark);

    /**
     * 管理员接口
     * @param id
     * @return
     */
    boolean removeRemarkById(long id);

    /**
     * 用户接口
     * @param remarkDeleteRequest
     * @return
     */
    boolean  removeRemarkById(RemarkDeleteRequest remarkDeleteRequest);

    Remark getRemarkById(long id);

    List<Remark> getListRemark(Remark remarkQuery);

    Page<RemarkVo> listPage(RemarkQueryRequest remarkQueryRequest);

    Long listCount(RemarkQueryRequest remarkQueryRequest);
}
