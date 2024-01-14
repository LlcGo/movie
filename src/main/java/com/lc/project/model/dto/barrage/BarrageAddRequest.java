package com.lc.project.model.dto.barrage;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 发送弹幕请求
 *
 * @author Lc
 */
@Data
public class BarrageAddRequest implements Serializable {


    /**
     *
     */
    private Integer movieId;

    /**
     *
     */
    private String userId;

    /**
     * 内容
     */
    private String content;

    /**
     * 弹幕出现时间
     */
    private String appTime;

    /**
     * 弹幕颜色
     */
    private String color;



}