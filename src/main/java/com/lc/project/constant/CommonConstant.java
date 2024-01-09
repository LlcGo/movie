package com.lc.project.constant;

/**
 * 通用常量
 *
 * @author Lc
 */
public interface CommonConstant {

    /**
     * 升序
     */
    String SORT_ORDER_ASC = "ascend";

    /**
     * 降序
     */
    String SORT_ORDER_DESC = "descend";

    /**
     * 下载位置
     */
    String  UPLOADS_ABSOLUTE_PATH = "/src/main/resources/static/uploads/";
    String UPLOADS_PATH = System.getProperty("user.dir") + UPLOADS_ABSOLUTE_PATH;



}
