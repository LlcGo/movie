package com.lc.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lc.project.common.ErrorCode;
import com.lc.project.constant.CommonConstant;
import com.lc.project.exception.BusinessException;
import com.lc.project.mapper.MovieMapper;
import com.lc.project.model.dto.movie.MovieQueryRequest;
import com.lc.project.model.entity.*;
import com.lc.project.model.vo.MovieVo;
import com.lc.project.service.*;
import io.swagger.models.auth.In;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static com.lc.project.websocket.ChatHandler.threadPoolExecutor;

/**
* @author asus
* @description 针对表【movie(电影表)】的数据库操作Service实现
* @createDate 2024-01-06 11:09:38
*/
@Service
public class MovieServiceImpl extends ServiceImpl<MovieMapper, Movie>
    implements MovieService{

    @Resource
    private UsersService userService;

    @Resource
    private MovieMapper movieMapper;

    @Resource
    @Lazy
    private PurchasedService purchasedService;




    @Override
    public void validMovie(Movie movie, boolean add) {
        if (movie == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Integer movieId = movie.getId();
        String movieName = movie.getMovieName();
        Integer type = movie.getType();
        Integer nation = movie.getNation();
        Integer year = movie.getYear();
        String movieProfile = movie.getMovieProfile();
        // 创建时，所有参数必须非空
        if (add) {
            //前面判斷string 後面判斷 Integer
            if (StringUtils.isAnyBlank(movieName,movieProfile) || ObjectUtils.anyNull(type, year,nation)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
        }
        if (StringUtils.isNotBlank(movieProfile) && movieProfile.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
        }
        if(movieId != null && movieId < 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "修改id出錯");
        }
    }

    @Override
    public Page<Movie> listPage(MovieQueryRequest movieQueryRequest) {
        Movie movieQuery = new Movie();
        BeanUtils.copyProperties(movieQueryRequest, movieQuery);
        long current = movieQueryRequest.getCurrent();
        long size = movieQueryRequest.getPageSize();
        String sortField = movieQueryRequest.getSortField();
        String sortOrder = movieQueryRequest.getSortOrder();
        String movieName = movieQuery.getMovieName();
        Integer type = movieQueryRequest.getType();
        Integer nation = movieQueryRequest.getNation();
        Integer year = movieQueryRequest.getYear();
        Boolean isScore = movieQueryRequest.getScore();
        Boolean isHot = movieQueryRequest.getHot();
//        //根据作者id查询
//        Integer directorId = movieQueryRequest.getDirectorId();
//        //根据演员id查询
//        Integer actorId = movieQueryRequest.getActorId();
//        //根据管理员查询
//        String userId = movieQueryRequest.getUserId();
        //根据创建时间查询
        Date creatTime = movieQueryRequest.getCreatTime();
        //根据是否上架查询
        Integer state = movieQueryRequest.getState();
        // content 需支持模糊搜索
        movieQuery.setMovieName(movieName);
        movieQuery.setType(type);
        movieQuery.setNation(nation);
        movieQuery.setYear(year);
//        movieQuery.setDirectorId();
//        movieQuery.setActorId();
//        movieQuery.setUserId();
        movieQuery.setCreatTime(creatTime);
        movieQuery.setState(state);
        // 限制爬虫
        if (size > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //可以根据type name nation year 查询
        QueryWrapper<Movie> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(movieName), "movieName", movieName);
        queryWrapper.eq(type!=null,"type",type);
        queryWrapper.eq(nation!= null,"nation",nation);
        queryWrapper.eq(year!=null,"year",year);
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        queryWrapper.orderByDesc(isScore != null && isScore,"score");
        queryWrapper.orderByDesc(isHot != null && isHot,"hot");
        return this.page(new Page<>(current, size), queryWrapper);
    }

    @Override
    public List<Movie> getListMovie(Movie movieQuery) {
        QueryWrapper<Movie> queryWrapper = new QueryWrapper<>(movieQuery);
        return this.list(queryWrapper);
    }

    @Override
    public Movie getMovieById(long id) {
        return this.getById(id);
    }

    @Override
    public boolean toUpdate(Movie movie) {
        long id = movie.getId();
        // 判断是否存在
        Movie oldMovie = this.getById(id);
        if (oldMovie == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return this.updateById(movie);
    }

    @Override
    public boolean removeMovieById(long id) {
        return this.removeById(id);
    }

    @Override
    public Integer toAddMovie(Movie movie) {
        boolean result = this.save(movie);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        return movie.getId();
    }

    @Override
    public void increaseHot(Integer id) {
        Movie oldMovie = this.getById(id);
        Double hot = oldMovie.getHot();
        oldMovie.setHot(++hot);
        this.updateById(oldMovie);
    }

    @Override
    public ConcurrentHashMap<Integer,List<Movie>> listIndexPage() {

        ConcurrentHashMap<Integer, List<Movie>> movieList = new ConcurrentHashMap<>();

        CompletableFuture<Boolean> future01 = CompletableFuture.supplyAsync(() -> selectMovieByType(1,movieList),threadPoolExecutor);

        CompletableFuture<Boolean> future02 = CompletableFuture.supplyAsync(() -> selectMovieByType(2,movieList),threadPoolExecutor);

        CompletableFuture<Boolean> future03 = CompletableFuture.supplyAsync(() -> selectMovieByType(3,movieList),threadPoolExecutor);

        CompletableFuture<Boolean> future04 = CompletableFuture.supplyAsync(() -> selectMovieByType(4,movieList),threadPoolExecutor);

        CompletableFuture<Boolean> future05 = CompletableFuture.supplyAsync(() -> selectMovieByType(5,movieList),threadPoolExecutor);

        CompletableFuture<Boolean> future06 = CompletableFuture.supplyAsync(() -> selectMovieByType(6,movieList),threadPoolExecutor);

        CompletableFuture<Boolean> future07 = CompletableFuture.supplyAsync(() -> selectMovieByType(7,movieList),threadPoolExecutor);

        CompletableFuture<Boolean> future08 = CompletableFuture.supplyAsync(() -> selectMovieByType(8,movieList),threadPoolExecutor);

        CompletableFuture<Boolean> future09 = CompletableFuture.supplyAsync(() -> selectMovieByType(9,movieList),threadPoolExecutor);

        CompletableFuture<Boolean> future010 = CompletableFuture.supplyAsync(() -> selectMovieByType(10,movieList),threadPoolExecutor);

        CompletableFuture<Boolean> future011 = CompletableFuture.supplyAsync(() -> selectMovieByType(11,movieList),threadPoolExecutor);

        CompletableFuture<Void> future = CompletableFuture.allOf(future01, future02,future03,future04,future05,future06,future07,future08,future09,future010,future011);
        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        return movieList;
    }

    @Override
    public List<Movie> getHotByType(Integer type) {
       return movieMapper.getMovieHotListByType(type);
    }

    @Override
    public Movie getMovieAndTypeNameById(long id) {
        Users loginUser = userService.getLoginUser();
        if(loginUser == null){
            return movieMapper.getMovieAndTypeNameById(id);
        }
        String currentUserId = loginUser.getId();
        QueryWrapper<Purchased> purchasedQueryWrapper = new QueryWrapper<>();
        purchasedQueryWrapper.eq("userId",currentUserId);
        purchasedQueryWrapper.eq("movieId",id);
        long count = purchasedService.count(purchasedQueryWrapper);
        Movie movie = movieMapper.getMovieAndTypeNameById(id);
        movie.setBuy(count > 0);
        return movie;
    }


    @Override
    public List<Movie> getHotMovie() {
      return movieMapper.getAllHotMovieOrderByHot();
    }

    public Boolean selectMovieByType(int type,ConcurrentHashMap<Integer, List<Movie>> movieList){
        List<Movie> movies = movieMapper.getMovieIndexListByType(type);
        movieList.put(type,movies);
        return true;
    }


}





