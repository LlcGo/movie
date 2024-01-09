package com.lc.project.model.enums;

import com.lc.project.common.ErrorCode;
import com.lc.project.exception.BusinessException;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 电影地区枚举
 *
 * @author Lc
 */
public enum MovieTypeEnum {
    /**
     * 0 战争片
     1 奇幻片
     2 科幻片
     3 剧情片
     4 恐怖片
     5 爱情片
     6 动作片
     7 喜剧片
     8 动画片
     9 悬疑片
     10 纪录片
     */
    WAR_FILM("战争片", 0),
    FANTASY_FILM("奇幻片", 1),
    SCIENCE_FICTION_FILM("科幻片", 2),
    FEATURE_FILM("剧情片", 3),
    HORROR_FILM("恐怖片", 4),
    ROMANTIC_FILM("爱情片", 5),
    ACTION_MOVIE("动作片", 6),
    COMEDY("喜剧片", 7),
    ANIMATED_FILM("动画片", 8),
    SUSPENSE_FILM("悬疑片", 9),
    DOCUMENTARY("纪录片",10),
    ALL("全部",11);


    private final String text;

    private final int value;

    MovieTypeEnum(String text, int value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<Integer> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    public int getValueByText(String text) {
        for (MovieTypeEnum yearEnum : MovieTypeEnum.values()) {
            if (Objects.equals(yearEnum.getText(),text)) {
                return yearEnum.value;
            }
        }
        throw new BusinessException(ErrorCode.PARAMS_ERROR,"枚举参数不正常");
    }

    public String getTextByValue(Integer value) {
        for (MovieTypeEnum yearEnum : MovieTypeEnum.values()) {
            if (Objects.equals(yearEnum.getValue(),value)) {
                return yearEnum.text;
            }
        }
        throw new BusinessException(ErrorCode.PARAMS_ERROR,"枚举参数不正常");
    }

    public int getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
