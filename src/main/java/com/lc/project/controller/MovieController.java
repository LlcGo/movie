package com.lc.project.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lc.project.annotation.AuthCheck;
import com.lc.project.common.BaseResponse;
import com.lc.project.common.DeleteRequest;
import com.lc.project.common.ErrorCode;
import com.lc.project.common.ResultUtils;
import com.lc.project.exception.BusinessException;
import com.lc.project.model.dto.movie.MovieAddRe;
import com.lc.project.model.dto.movie.MovieAddRequest;
import com.lc.project.model.dto.movie.MovieQueryRequest;
import com.lc.project.model.dto.movie.MovieUpdateRequest;
import com.lc.project.model.entity.Movie;
import com.lc.project.model.entity.Users;
import com.lc.project.model.vo.MovieVo;
import com.lc.project.service.MovieService;
import com.lc.project.service.UsersService;
import com.lc.project.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import static com.lc.project.constant.CommonConstant.*;
import static com.lc.project.websocket.ChatHandler.threadPoolExecutor;

/**
 * 電影
 *
 * @author Lc
 */
@RestController
@RequestMapping("/movie")
@Slf4j
public class MovieController {

    @Resource
    private MovieService movieService;

    @Resource
    private UsersService userService;

    @Resource
    private RedisUtils redisUtils;



    /**
     * 创建
     *
     * @param movieAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Integer> addMovie(@RequestBody MovieAddRequest movieAddRequest, HttpServletRequest request) {
        if (movieAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Movie movie = new Movie();
        BeanUtils.copyProperties(movieAddRequest, movie);
        // 校验directorName
        movieService.validMovie(movie, true);
        Users loginUser = userService.getLoginUser(request);
        movie.setUserId(loginUser.getId());
        Integer newMovieId =  movieService.toAddMovie(movie);
        return ResultUtils.success(newMovieId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteMovie(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = deleteRequest.getId();
        // 判断是否存在
        Movie oldMovie = movieService.getById(id);
        if (oldMovie == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可删除
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = movieService.removeMovieById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新
     *
     * @param movieUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateMovie(@RequestBody MovieUpdateRequest movieUpdateRequest,
                                            HttpServletRequest request) {
        if (movieUpdateRequest == null || movieUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //管理员可修改
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        Movie movie = new Movie();
        BeanUtils.copyProperties(movieUpdateRequest, movie);
        // 参数校验
        movieService.validMovie(movie, false);
        boolean result = movieService.toUpdate(movie);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<MovieVo> getMovieById(long id) {
        Users loginUser = userService.getLoginUser();
        //如果用户没有登录
        if(loginUser == null){
            Movie movie = movieService.getMovieAndTypeNameById(id);
            MovieVo movieVo = new MovieVo();
            BeanUtils.copyProperties(movie,movieVo);
            return ResultUtils.success(movieVo);
        }
        String currentUserId = loginUser.getId();
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Movie movie = movieService.getMovieAndTypeNameById(id);
        MovieVo movieVo = new MovieVo();
        BeanUtils.copyProperties(movie,movieVo);
        //查看是否已经收藏
        movieVo.setFavorite(redisUtils.sHasKey(REDIS_FA_MOVIE + id,currentUserId));
        return ResultUtils.success(movieVo);
    }

    /**
     * 获取列表（仅管理员可使用）
     *
     * @param movieQueryRequest
     * @return
     */
    @AuthCheck(mustRole = "admin")
    @GetMapping("/list")
    public BaseResponse<List<Movie>> listMovie(MovieQueryRequest movieQueryRequest) {
        Movie movieQuery = new Movie();
        if (movieQueryRequest != null) {
            BeanUtils.copyProperties(movieQueryRequest, movieQuery);
        }
        List<Movie> movieList = movieService.getListMovie(movieQuery);
        return ResultUtils.success(movieList);
    }

    /**
     * 分页获取列表
     *
     * @param movieQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<Movie>> listMovieByPage(@RequestBody MovieQueryRequest movieQueryRequest, HttpServletRequest request) {
        if (movieQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Integer year = movieQueryRequest.getYear();
        Integer type = movieQueryRequest.getType();
        Integer nation = movieQueryRequest.getNation();
        if(year != null && year == 0){
            movieQueryRequest.setYear(null);
        }
        if(type!= null && type == 0){
            movieQueryRequest.setType(null);
        }
        if(nation!= null && nation == 0){
            movieQueryRequest.setNation(null);
        }
        Page<Movie> moviePage = movieService.listPage(movieQueryRequest);
        return ResultUtils.success(moviePage);
    }

    @PostMapping("/hot")
    public void addHot(Integer movieId){
        if(movieId < 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        CompletableFuture.runAsync(()->movieService.increaseHot(movieId),threadPoolExecutor);
    }


    @GetMapping("/list/index/page")
    public BaseResponse<ConcurrentHashMap<Integer,List<Movie>>> listIndexMovieByPage() {
        ConcurrentHashMap<Integer,List<Movie>> moviePage = movieService.listIndexPage();
        return ResultUtils.success(moviePage);
    }

    @GetMapping("/getHot")
    public BaseResponse<List<Movie>> getHotByType(Integer type){
        if (type == null || type < 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<Movie> movieList = movieService.getHotByType(type);
        return ResultUtils.success(movieList);
    }

    @GetMapping("/get/hotMovie")
    public BaseResponse<List<Movie>> getHotMovie(){
        List<Movie> movieList = movieService.getHotMovie();
        return ResultUtils.success(movieList);
    }

    @PostMapping("/set/state")
    public BaseResponse<Boolean> setState(Integer state,Integer movieId,Boolean flag){
        Boolean update = movieService.setState(state,movieId,flag);
        return ResultUtils.success(update);
    }

    @PostMapping("/set/mf")
    public BaseResponse<Boolean> setMf(Integer state,Integer movieId){
        Boolean update = movieService.setMf(state,movieId);
        return ResultUtils.success(update);
    }

    @PostMapping("/set/price")
    public BaseResponse<Boolean> setPrice(Integer movieId,Integer price){
        Boolean update = movieService.setPrice(price,movieId);
        return ResultUtils.success(update);
    }

    @PostMapping("/set/TypeRe")
    public BaseResponse<Boolean> setTypeRe(String state){
        boolean typeRe = redisUtils.set(TYPE_RE, state);
        return ResultUtils.success(typeRe);
    }

    @PostMapping("/set/SearchRe")
    public BaseResponse<Boolean> setSearchRe(String state){
        boolean typeSearch = redisUtils.set(SEARCH_RE, state);
        return ResultUtils.success(typeSearch);
    }

    @PostMapping("/get/TypeRe")
    public BaseResponse<String> getTypeRe(){
        Object o = redisUtils.get(TYPE_RE);
        String re = "1";
        if(o != null){
            re = (String) o;
        }
        return ResultUtils.success(re);
    }

    @PostMapping("/get/SearchRe")
    public BaseResponse<String> getSearchRe(){
        Object o = redisUtils.get(SEARCH_RE);
        String re = "1";
        if(o != null){
            re = (String) o;
        }
        return ResultUtils.success(re);
    }

    @PostMapping("/set/SyRe")
    public BaseResponse<Boolean> setSyRe(@RequestBody MovieAddRe movie, String state){
        if(movie == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Boolean flag = movieService.SyRe(movie,state);
        return ResultUtils.success(flag);
    }

    @PostMapping("/get/SyRe")
    public BaseResponse<Map<Object, Object>> getSyRe(){
        Map<Object, Object> re = movieService.getRe();
        return ResultUtils.success(re);
    }

    @PostMapping("/get/ids")
    public BaseResponse<List<Movie>> getSyReMovieById(Integer id,Integer id2,Integer id3){
        QueryWrapper<Movie> movieQueryWrapper = new QueryWrapper<>();
        movieQueryWrapper.in("id",id,id2,id3);
        List<Movie> list = movieService.list(movieQueryWrapper);
        return ResultUtils.success(list);
    }


    @GetMapping("/getHot/EChars")
    public BaseResponse<Map<Integer, List<Movie>>> getHotEChars(){
//        if (type == null || type < 0){
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
        Map<Integer, List<Movie>> movieList = movieService.getHotEChars();
        return ResultUtils.success(movieList);
    }

    @GetMapping("/getScore/EChars")
    public BaseResponse<Map<Integer, List<Movie>>> getScoreEChars(){
//        if (type == null || type < 0){
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
        Map<Integer, List<Movie>> movieList = movieService.getScoreEChars();
        return ResultUtils.success(movieList);
    }


    @GetMapping("/getScoreAndScore/EChars")
    public BaseResponse<?> getScoreAndScoreEChars(){
//        if (type == null || type < 0){
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
        HashMap<Integer, List<Integer>> movieList = movieService.getScoreAndScoreEChars();
        return ResultUtils.success(movieList);
    }

    @GetMapping("/allByType/EChars")
    public BaseResponse<Map<Integer, List<Movie>>> getAllByTypeEChars(){
//        if (type == null || type < 0){
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
        Map<Integer, List<Movie>> movieList = movieService.getAllByTypeEChars();
        return ResultUtils.success(movieList);
    }

}
