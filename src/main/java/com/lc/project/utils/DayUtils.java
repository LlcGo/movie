package com.lc.project.utils;

import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.util.Calendar;
import java.util.Date;

@Component
public class DayUtils {

    /**
     * 获取今年多少年
     * @return
     */
    public int getCurrentYear(){
        return LocalDate.now().lengthOfYear();
    }


    /**
     * 当前季度多少天
     * @return
     * @throws ParseException
     */
    public int getCurrentQuarter()  {
        LocalDate today=LocalDate.now();
        Month month = today.getMonth();
        Month firstMonthOfQuarter = month.firstMonthOfQuarter();
        Month endMonthOfQuarter = Month.of(firstMonthOfQuarter.getValue() + 2);
        LocalDate start1 = LocalDate.of(today.getYear(), firstMonthOfQuarter, 1);
        LocalDate end1 = LocalDate.of(today.getYear(), endMonthOfQuarter, endMonthOfQuarter.length(today.isLeapYear()));
        System.out.println(start1 + ":" + end1);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date d1 = null;
        Date d2 = null;
        try {
            d1 = df.parse(start1.toString());
            d2 = df.parse(end1.toString());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        long diff = d2.getTime() - d1.getTime();//这样得到的差值是毫秒级别
        long days = diff / (1000 * 60 * 60 * 24);
        return (int) days;
    }

    /**
     * 当前月份多少天
     * @return
     */
    public int getCurrentMonth(){
        Calendar a = Calendar.getInstance();
        a.set(Calendar.DATE, 1);//把日期设置为当月第一天
        a.roll(Calendar.DATE, -1);//日期回滚一天，也就是最后一天
        return a.get(Calendar.DATE);
    }

}
