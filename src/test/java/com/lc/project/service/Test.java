package com.lc.project.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lc.project.utils.Aig;
import com.lc.project.utils.RedisUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Test {
    public static void main(String[] args) {
        List<String> strings1 = Arrays.asList("java","男","大一","乒乓");
        List<String> strings2 = Arrays.asList("java","男","大一","乒乓");
        List<String> strings3 = Arrays.asList("java","大三","女");

        Gson gson = new Gson();

        String tags = "[Java,男]";
        List<String> tagList = gson.fromJson(tags, new TypeToken<List<String>>() {
        }.getType());

        int i1 = Aig.minDistance(strings1, strings2);
        int i2 = Aig.minDistance(strings1, strings3);
        System.out.println(strings1.equals(strings3));
        System.out.println(i2);
    }





}
