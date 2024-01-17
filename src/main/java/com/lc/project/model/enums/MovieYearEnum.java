package com.lc.project.model.enums;

import com.lc.project.common.ErrorCode;
import com.lc.project.exception.BusinessException;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 电影年代别枚举
 *
 * @author Lc
 */
public enum MovieYearEnum {
    /**
     * 电影年代
     0 2023
     1 2022
     2 2021
     3 2020
     4 2019
     5 2018
     6 2017
     7 2016
     8 2015
     9 2014
     10 2013
     11 2012
     12 2011
     13 2010
     14 00年代（2000-2009）
     15 90年代（1990-1999）
     16
     */
    TWO_ZERO_TOW_TREE("2023", 0),
    TWO_ZERO_TOW_TWO("2022", 1),
    TWO_ZERO_TOW_ONE("2021", 2),
    TWO_ZERO_TOW_ZERO("2020", 3),
    TWO_ZERO_ONE_NINE("2019", 4),
    TWO_ZERO_ONE_EIGHT("2018", 5),
    TWO_ZERO_ONE_SEVEN("2017", 6),
    TWO_ZERO_ONE_SIX("2016", 7),
    TWO_ZERO_ONE_FIVE("2015", 8),
    TWO_ZERO_ONE_FOUR("2014", 9),
    TWO_ZERO_ONE_THREE("2013", 10),
    TWO_ZERO_ONE_TWO("2012", 11),
    TWO_ZERO_ONE_ONE("2011", 12),
    TWO_ZERO_ONE_ZERO("2010", 13),
    ZERO_ZERO("00年代(2000-2009)", 14),
    NINE("90年代(1990-1999)", 15),
    EARLIER("更早(1979之前)", 16),
    ALL("全部",17);


    private final String text;

    private final int value;

    MovieYearEnum(String text, int value) {
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
        for (MovieYearEnum yearEnum : MovieYearEnum.values()) {
            if (Objects.equals(yearEnum.getText(),text)) {
                return yearEnum.value;
            }
        }
        throw new BusinessException(ErrorCode.PARAMS_ERROR,"枚举参数不正常");
    }

    public String getTextByValue(Integer value) {
        for (MovieYearEnum yearEnum : MovieYearEnum.values()) {
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
