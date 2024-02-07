package com.lc.project.service;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lc.project.common.PageRequest;
import com.lc.project.mapper.*;
import com.lc.project.model.dto.movie.MovieQueryRequest;
import com.lc.project.model.entity.*;
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
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@SpringBootTest
public class MySqlTest {

    @Resource
    private MovieService movieService;

    @Resource
    private VipService vipService;

    @Resource
    private RecentChatMapper recentChatMapper;

    @Resource
    private RemarkUserMapper remarkUserMapper;

    @Resource
    private RemarkMapper remarkMapper;

    @Resource
    private FriendsRequestMapper friendsRequestMapper;

    @Resource
    private MyFriendsMapper myFriendsMapper;

    @Resource
    private ChatMsgMapper chatMsgMapper;

    @Resource
    private MovieMapper movieMapper;

    @Resource
    private UsersService usersService;

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private PurchasedMapper purchasedMapper;

    @Resource
    private FavoritesMapper favoritesMapper;

    @Resource
    private VipMapper vipMapper;

    @Resource
    private MovieTypeService movieTypeService;

    @Resource
    private OrderService orderService;
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
        Movie movie= movieService.getMovieById(2);
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

    @Test
    public void testMapper(){
        List<RecentChat> recentAndFriends = recentChatMapper.getRecentAndFriends("1741446004448710657");
        System.out.println(recentAndFriends);
    }

    @Test
    public void testMapper2(){
        List<FriendsRequest> requestByUserId = friendsRequestMapper.getRequestByUserId("1743141189096329218");
        System.out.println(requestByUserId);
    }

    @Test
    public void testMapper3(){
        List<MyFriends> myFriend = myFriendsMapper.getMyFriend(1741461358159978498L);
        System.out.println(myFriend);
    }

    @Test
    public void testMapper5(){
        int i = chatMsgMapper.updateByMyIdAndOtherId(1741446004448710657L, 1741461358159978498L);
        System.out.println(i);
    }

    @Test
    public void testMapper6(){
        CompletableFuture<Integer> future01 = CompletableFuture.supplyAsync(() -> {
            return friendsRequestMapper.updateRequestMessageToOne("1741461358159978498");
        });
        CompletableFuture<Integer> future02 = CompletableFuture.supplyAsync(() -> {
            return friendsRequestMapper.updateRequestMessageToTwo("1741461358159978498");
        });
        int count;
        try {
            Integer integer = future01.get();
            Integer integer1 = future02.get();
            count = integer + integer1;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        System.out.println(count);
    }

    @Test
    public void testMapper7(){
        List<Users> userList = usersService.list();
        String tags = userList.get(3).getLikeType();
        Gson gson = new Gson();
        List<String> tagList = gson.fromJson(tags, new TypeToken<List<String>>() {
        }.getType());
    }

    @Test
    public void testMapper8(){
        List<Order> orderList = orderMapper.getOrderAndMovieByUserId("1741446004448710657");
        System.out.println(orderList);
    }

    @Test
    public void testMapper9(){
        List<Purchased> purchAndMovieByUserId = purchasedMapper.getPurchAndMovieByUserId("1741446004448710657");
        System.out.println(purchAndMovieByUserId);
    }

    @Test
    public void testMapper10(){
        List<Favorites> myFavoritesAndMovieByUserId = favoritesMapper.getMyFavoritesAndMovieByUserId("1741446004448710657");
        System.out.println(myFavoritesAndMovieByUserId);
    }

    @Test
    public void testMapper11(){
        List<Movie> movieHotListByType = movieMapper.getMovieHotListByType(1);
        System.out.println(movieHotListByType);
    }

    @Test
    public void testMapper12(){
        List<ChatMsg> chatMsgList = chatMsgMapper.getChatAndOtherUsers(1741446004448710657L,1741461358159978498L);
        List<ChatMsg> chatMsgList2 = chatMsgMapper.getChatAndUsers(1741446004448710657L,1741461358159978498L);

        System.out.println(chatMsgList);
        System.out.println(chatMsgList2);
    }

    @Test
    public void testMapper13(){
        Integer current = 1;
        Integer pageSize = 7;
        current = (current - 1)* pageSize;
        Users loginUser = usersService.getLoginUser();
        List<Remark> page1 = remarkMapper.getRemarkAndUserPage(2,current,pageSize);
        Gson gson = new Gson();
        for (Remark remark : page1) {
            RemarkUser remarkUser = remark.getRemarkUser();
            //如果没有 RemarkId 代表没有评论过
            if (remarkUser.getRemarkId() == null || remarkUser.getSupport() == 0){
                remark.setLike(false);
                remark.setHate(false);
                continue;
            }
            //如果是我们的评论
            if (remarkUser.getUserId().equals("1741446004448710657")){
                Integer support = remarkUser.getSupport();
                //1.不支持 2 支持
                if (support == 1){
                    remark.setLike(false);
                    remark.setHate(true);
                }
                if(support == 2){
                    remark.setLike(true);
                    remark.setHate(false);
                }
            }
        }
        //根据remarkId 来确认是否有过评论
        System.out.println(gson.toJson(page1));
    }

    @Test
    public void t(){
        QueryWrapper<Vip> vipQueryWrapper = new QueryWrapper<>();
        vipQueryWrapper.eq("userId","1743141189096329218");
        Vip vip = vipMapper.selectOne(vipQueryWrapper);
        Date overTime = vip.getOverTime();
        Date date = new Date();
        boolean after = date.after(overTime);
        System.out.println(after);
    }

    @Test
    public void toEC(){
        List<Movie> movieList = movieMapper.getAllByTypeEChars();
//        Map<Integer, List<Movie>> collect1 = movieList.stream().collect(Collectors.groupingBy(Movie::getType));
        List<Movie> movieScoreToEChars = movieMapper.getMovieScoreToEChars();
        List<Movie> movieHotToEChars = movieMapper.getMovieHotToEChars();
        Map<Integer, List<Movie>> collect = movieScoreToEChars.stream().collect(Collectors.groupingBy(Movie::getType));
        Map<Integer, List<Movie>> collect2 = movieHotToEChars.stream().collect(Collectors.groupingBy(Movie::getType));
        HashMap<Integer, List<Integer>> integerIntegerHashMap = new HashMap<>();
        for (Map.Entry<Integer, List<Movie>> entry : collect.entrySet()) {
            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue().size());
            ArrayList<Integer> objects = new ArrayList<>();
            int size = entry.getValue().size();
            objects.add(size);
            integerIntegerHashMap.put(entry.getKey(),objects);
        }
        for (Map.Entry<Integer, List<Movie>> entry : collect2.entrySet()) {
            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue().size());
            for (Map.Entry<Integer, List<Integer>> entry2 : integerIntegerHashMap.entrySet()) {
               if(entry2.getKey().equals(entry.getKey())){
                   entry2.getValue().add(entry.getValue().size());
               }
            }
        }
        System.out.println(integerIntegerHashMap);
    }

    @Test
    public void toEC2(){
        List<Order> echars = orderMapper.getEchars();
        System.out.println(echars);
        ArrayList<Movie> movies = new ArrayList<>();
        echars.forEach(item -> {
            Movie movie = item.getMovie();
            if(movie.getId() != null){
                movies.add(movie);
            }
        });
        System.out.println(movies);
        Map<Integer, List<Movie>> collect = movies.stream().collect(Collectors.groupingBy(Movie::getType));
        System.out.println(collect);
    }

    @Test
    public void toEC3(){
        QueryWrapper<Order> orderQueryWrapper = new QueryWrapper<>();
        orderQueryWrapper.isNotNull("vipType");
        List<Order> orderList =  orderService.list(orderQueryWrapper);
        System.out.println(orderList);
        Map<Integer, List<Order>> collect = orderList.stream().collect(Collectors.groupingBy(Order::getVipType));
        System.out.println(collect);
    }
}
