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

    String UPLOADS_ABSOLUTE_IMG_PATH = "/src/main/resources/static/uploads/img/";

    String UPLOADS_IMG_PATH =  System.getProperty("user.dir") + UPLOADS_ABSOLUTE_IMG_PATH;



    /**
     * redis 收藏影视
     */

    String REDIS_FA_MOVIE = "movie:favorites:";

    /**
     * redis 设置的 订单延迟时间key
     */
    String ORDER_DDL_TIME = "DDL:Time";

    /**
     * redis 设置推荐
     */
    String TYPE_RE = "typeRe";

    String SEARCH_RE = "typeSearch";

    /**
     * 首页推荐key
     */
    String SY_TJ = "sytj";

    /**
     * 会员价格 key
     */
    String YK = "YK";
    String JK = "JK";
    String NK = "NK";
}
