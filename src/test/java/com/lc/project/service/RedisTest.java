package com.lc.project.service;

import com.lc.project.utils.RedisUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.HashMap;



@SpringBootTest
public class RedisTest {
    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private RedisUtils redisUtils;
    @Test
    public void test(){

       String user1 =   "1741446004448710657";
       String user2 =   "1741461358159978498";
       String user1current =  "1741446004448710657:1741461358159978498";
       String user2current =  "1741461358159978498:1741446004448710657";
       redisTemplate.opsForHash().put("userCurrent",user1,user1current);
       redisTemplate.opsForHash().put("userCurrent",user2,user2current);

        HashMap userCurrent = (HashMap) redisTemplate.opsForHash().entries("userCurrent");
        System.out.println(userCurrent);
//        String o = (String)userCurrent.get("1741446004448710657");
        Object o = userCurrent.get("1741446004448710657");
        System.out.println(o);
//        String[] split = o.split(":");
//        String o1 = (String)userCurrent.get("1741461358159978498");
        Object o1 = userCurrent.get("1741461358159978498");
//        String[] split2 = o1.split(":");
        System.out.println(o1);
//        if(split[0].equals(split2[1]) && split2[0].equals(split[1])){
//            System.out.println(true);
//        }
    }

    @Test
    public void testSet(){
        redisUtils.sSet("movie:favorites:" + 1 ,"121315646516");
        redisUtils.sSet("movie:favorites:" + 1 ,"121315646515");
        boolean b = redisUtils.sHasKey("movie:favorites:" + 1, "121315646516");
        System.out.println(b);
        boolean b1 = redisUtils.sHasKey("movie:favorites:" + 1, "6");
        System.out.println(b1);

        redisUtils.setRemove("movie:favorites:" + 1,"121315646516");
        boolean b3 = redisUtils.sHasKey("movie:favorites:" + 1, "121315646516");
        System.out.println(b3);
    }

    @Test
    public void testTime(){
        boolean ddlTime = redisUtils.set("DDL_Time", 6000);
        Object o = redisUtils.get("DDL_Time");
        System.out.println(o);
        redisUtils.set("DDL_Time",7000);
        Object o1 = redisUtils.get("DDL_Time");
        System.out.println(o1);
    }
}
