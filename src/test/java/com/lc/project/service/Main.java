package com.lc.project.service;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

public class Main {
    public static void main(String[] args) throws ParseException {

        Long time = 436340L;
        time = time / 1000;
        // 加上10秒
        Date next10Seconds = DateUtil.offsetSecond(new Date(), Math.toIntExact(time));
        System.out.println(next10Seconds);
//        LocalDateTime now = LocalDateTimeUtil.now();
//        System.out.println(now);
//        final LocalDateTime localDateTime = LocalDateTimeUtil.parse(now.toString());
//        // 增加一天
//        // "2020-01-24T12:23:56"
//        LocalDateTime offset = LocalDateTimeUtil.offset(localDateTime, 30, ChronoUnit.DAYS);
//        System.out.println(offset);
//        ZoneId zoneId = ZoneId.systemDefault();
//        ZonedDateTime zdt = offset.atZone(zoneId);
//        Date OverTime = Date.from(zdt.toInstant());
//        System.out.println("overTime---->"+OverTime);
//
//        //获取时间间隔
//        LocalDateTime start = LocalDateTimeUtil.parse("2024-02-01T00:00:00");
//        LocalDateTime end = LocalDateTimeUtil.parse("2024-03-01T00:00:00");
//        Duration between = LocalDateTimeUtil.between(start, end);
//        long days = between.toDays();
//        System.out.println(days);


//        Date date = new Date(System.currentTimeMillis());
//        String formatStr2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
//        System.out.println(formatStr2);
//        String now = DateUtil.now();
//        System.out.println(now);
//        String substring = now.substring(6, 7);
//        System.out.println(substring);
//        LocalDateTime parse = LocalDateTimeUtil.parse(now);
//        LocalDateTime offset2 = LocalDateTimeUtil.offset(parse, 30, ChronoUnit.DAYS);
//        System.out.println(offset2);

        //获取当前季度
//        int i = Calendar.getInstance().get(Calendar.MONTH) + 1;
//        int currentQuarter = (i - 1 ) / 3 + 1;
//        System.out.println("当前季度" + currentQuarter);



        //当前季度多少天
        LocalDate today=LocalDate.now();
        Month month = today.getMonth();
        Month firstMonthOfQuarter = month.firstMonthOfQuarter();
        Month endMonthOfQuarter = Month.of(firstMonthOfQuarter.getValue() + 2);
        LocalDate start1 = LocalDate.of(today.getYear(), firstMonthOfQuarter, 1);
        LocalDate end1 = LocalDate.of(today.getYear(), endMonthOfQuarter, endMonthOfQuarter.length(today.isLeapYear()));
        System.out.println(start1 + ":" + end1);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date d1 = df.parse(start1.toString());
        Date d2 = df.parse(end1.toString());
        long diff = d2.getTime() - d1.getTime();//这样得到的差值是毫秒级别
        long days1 = diff / (1000 * 60 * 60 * 24);
        System.out.println(days1);


        //当月多少天
        Calendar a = Calendar.getInstance();
        a.set(Calendar.DATE, 1);//把日期设置为当月第一天
        a.roll(Calendar.DATE, -1);//日期回滚一天，也就是最后一天
        int maxDate = a.get(Calendar.DATE);
        System.out.println(maxDate);

        Date date = new Date();
        Instant instant = date.toInstant();
        ZoneId zoneId2 = ZoneId.systemDefault();

        LocalDateTime localDateTime2 = instant.atZone(zoneId2).toLocalDateTime();
        System.out.println("Date = " + date);
        System.out.println("LocalDateTime = " + localDateTime2);

    }
}
