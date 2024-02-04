package com.lc.project.model.dto.barrage;

import com.lc.project.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户查询请求
 *
 * @author Lc
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BarrageQueryRequest extends PageRequest implements Serializable {


    /**
     * 用户昵称
     */
    private String nickName;

    private String movieName;

    private String content;


    private static final long serialVersionUID = 1L;
}