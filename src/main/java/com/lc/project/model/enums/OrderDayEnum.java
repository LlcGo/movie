package com.lc.project.model.enums;

import com.lc.project.common.ErrorCode;
import com.lc.project.exception.BusinessException;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 帖子性别枚举
 *
 * @author Lc
 */
public enum OrderDayEnum {

    MONTHS("月", 0),
    QUARTER("季度", 1),
    YEAR("年", 2);

    private final String text;

    private final int value;

    OrderDayEnum(String text, int value) {
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

    public static Integer getValueByText(String text) {
        for (OrderDayEnum yearEnum : OrderDayEnum.values()) {
            if (Objects.equals(yearEnum.getText(),text)) {
                return yearEnum.value;
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
