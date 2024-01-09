package com.lc.project.service;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lc.project.model.dto.movie.MovieQueryRequest;
import com.lc.project.model.entity.Movie;
import com.lc.project.model.entity.Vip;
import com.lc.project.model.vo.MovieVo;
import com.lc.project.service.MovieService;
import com.lc.project.service.VipService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@SpringBootTest
public class MySqlTest {

    @Resource
    private MovieService movieService;

    @Resource
    private VipService vipService;

    @Test
    public void test(){
        MovieQueryRequest movieQueryRequest = new MovieQueryRequest();
        movieQueryRequest.setScore(true);
        movieQueryRequest.setSortOrder("ascend");
        movieQueryRequest.setSortField("creatTime");
//        movieQueryRequest.setHot(false);
//        movieQueryRequest.setCurrent(0L);
//        movieQueryRequest.setPageSize(0L);
//        movieQueryRequest.setSortField("");
//        movieQueryRequest.setSortOrder("");
        Page<Movie> moviePage = movieService.listPage(movieQueryRequest);
        List<Movie> records = moviePage.getRecords();
        System.out.println(records);
    }

    @Test
    public void test2(){
        movieService.increaseHot(2);
    }

    @Test
    public void testById(){
        MovieVo movie= movieService.getMovieById(2);
        System.out.println(movie);
        Date creatTime = movie.getCreatTime();
        System.out.println("时间------------>" + creatTime);

        //时间转换
        Instant instant = creatTime.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
        //一个月 三个月（季度） 一年

    }

    @Test
    public void testInsertTime(){
        Vip vip = new Vip();
        LocalDateTime now = LocalDateTimeUtil.now();
//        System.out.println(now);
        final LocalDateTime localDateTime = LocalDateTimeUtil.parse(now.toString());
        // 增加一天
        // "2020-01-24T12:23:56"
        LocalDateTime offset = LocalDateTimeUtil.offset(localDateTime, 30, ChronoUnit.DAYS);
        System.out.println(offset);
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = offset.atZone(zoneId);
        Date OverTime = Date.from(zdt.toInstant());
        System.out.println("overTime---->"+OverTime);

        vip.setOverTime(OverTime);
        vipService.save(vip);
    }

}
