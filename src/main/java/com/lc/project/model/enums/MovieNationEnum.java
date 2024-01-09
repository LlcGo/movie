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
public enum MovieNationEnum {
    /**
     * 0 中国
     1 美国
     2 中国香港
     3 韩国
     4 日本
     5 法国
     6 英国
     7 德国
     8 泰国
     9 印度
     */
    CHINESE("中国", 0),
    USA("美国", 1),
    CHINESE_HONG_KONG("中国香港", 2),
    KOREA("韩国", 3),
    JAPAN("日本", 4),
    FRANCE("法国", 5),
    BRITAIN("英国", 6),
    GERMANY("德国", 7),
    THAILAND("泰国", 8),
    INDIA("印度", 9),
    ALL("全部",10);


    private final String text;

    private final int value;

    MovieNationEnum(String text, int value) {
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

    public static int getValueByText(String text) {
        for (MovieNationEnum yearEnum : MovieNationEnum.values()) {
            if (Objects.equals(yearEnum.getText(),text)) {
                return yearEnum.value;
            }
        }
        throw new BusinessException(ErrorCode.PARAMS_ERROR,"枚举参数不正常");
    }

    public static String getTextByValue(Integer value) {
        for (MovieNationEnum yearEnum : MovieNationEnum.values()) {
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
