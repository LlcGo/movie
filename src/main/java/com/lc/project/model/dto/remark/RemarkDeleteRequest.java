package com.lc.project.model.dto.remark;

import io.swagger.models.auth.In;
import lombok.Data;

import java.io.Serializable;

/**
 * 订单请求
 *
 * @author Lc
 */
@Data
public class RemarkDeleteRequest implements Serializable {


    /**
     *
     */
    private String userId;

    /**
     *
     */
    private Integer movieId;


    /**
     * id
     */
    private Integer id;



}