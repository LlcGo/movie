package com.lc.project.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

//indexName 索引名称，动态创建索引，所以索引的名称为传递过来的名称。esConfig为索引名称类，再执行过程中会将索引名称传递到esConfig中，进一步将实体SubstanceSearch的名称赋值
@Data
@FieldNameConstants
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "#{esConfig.getApiCallIndexName()}",createIndex = false)
public class SubstanceSearch {
    @Id
    private long id;



    @Field(type = FieldType.Text)
    private String name;


    @Field(type = FieldType.Text)
    private String displayName;

}