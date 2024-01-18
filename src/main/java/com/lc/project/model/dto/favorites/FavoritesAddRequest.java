package com.lc.project.model.dto.favorites;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 收藏请求
 *
 * @author Lc
 */
@Data
public class FavoritesAddRequest implements Serializable {


    /**
     *
     */
    private Integer movieId;



    private static final long serialVersionUID = 1L;
}