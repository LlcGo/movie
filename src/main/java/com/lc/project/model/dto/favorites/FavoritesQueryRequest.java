package com.lc.project.model.dto.favorites;

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
public class FavoritesQueryRequest extends PageRequest implements Serializable {

    private String userId;

    private static final long serialVersionUID = 1L;
}