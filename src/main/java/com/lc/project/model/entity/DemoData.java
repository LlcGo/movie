package com.lc.project.model.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@EqualsAndHashCode
public class DemoData {
    @ExcelProperty(value = "序号")
    private String id;
    @ExcelProperty(value = "资源名称")
    private String name;
    @ExcelProperty(value = "资源中文名")
    private String chineseName;
    @ExcelProperty(value = "数据项名称")
    private String projectName;
    @ExcelProperty(value = "数据项中文名称")
    private String projectChineseName;
    @ExcelProperty(value = "数据项类型")
    private String type;
    @ExcelProperty(value = "数据项长度")
    private Integer length;
    @ExcelProperty(value = "是否主键")
    private String Pkey;
    @ExcelProperty(value = "是否索引")
    private String index;
    @ExcelProperty(value = "敏感级别")
    private String mg;
    @ExcelProperty(value = "数据元")
    private String sjy;
    @ExcelProperty(value = "限定词")
    private String xdc;
}