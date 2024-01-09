package com.lc.project.scheludleTask;

import com.lc.project.model.entity.Movie;
import com.lc.project.model.entity.Remark;
import com.lc.project.service.MovieService;
import com.lc.project.service.RemarkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 更新影评分数
 */

@Configuration
@Slf4j
public class ScheduleTask {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Resource
    private MovieService movieService;

    @Resource
    private RemarkService remarkService;
    //测试每各5秒执行
//    @Scheduled(cron = "0/5 * * * * ?")
//    每天整点执行一次
//    @Scheduled(cron = "0 0 0/1 * * ?")
//    每天半夜12点30分执行一次：0 30 0 * * ?
//    @Scheduled(cron = "0 30 0 * * ?")
    public void updateMovieScore() {
        List<Movie> list = movieService.list();
        List<Remark> remarkList = remarkService.list();
        //每个电影出现的评分
        HashMap<Integer, Integer> movieIdAndScore = new HashMap<>();
        //每个电影评分出现的次数 Integer为出现的次数
        HashMap<Integer, Integer> movieTotalNum = new HashMap<>();
        remarkList.forEach(item->{
            boolean flag = true;
            boolean scoreFlag = true;
            Integer movieId = item.getMovieId();
            Integer score = item.getScore();
            //计算每个电影出现的次数
            if(movieTotalNum.containsKey(movieId)){
                flag = false;
                Integer num = movieTotalNum.get(movieId);
                num++;
                movieTotalNum.put(movieId,num);
            }
            if(flag){
                //第一次 + 1
                movieTotalNum.put(movieId,1);
            }
            //每个电影的评分相加
            if(movieIdAndScore.containsKey(movieId)){
                Integer oldScore = movieIdAndScore.get(movieId);
                score = score + oldScore;
                movieIdAndScore.put(movieId,score);
                scoreFlag = false;
            }
            if(scoreFlag){
                movieIdAndScore.put(movieId,score);
            }
        });
        //俩个map得出最后的平均分
        Iterator<Map.Entry<Integer, Integer>> entries = movieTotalNum.entrySet().iterator();
        HashMap<Integer, Double> finalScore = new HashMap<>();
        while (entries.hasNext()) {
            Map.Entry<Integer, Integer> movieTotalNumEntry = entries.next();
            Integer movieId = movieTotalNumEntry.getKey();
            Integer movieTotal = movieTotalNumEntry.getValue();
            if(movieIdAndScore.containsKey(movieId)){
                Integer score = movieIdAndScore.get(movieId);
                double result = (double) score / movieTotal;
                DecimalFormat decimalFormat = new DecimalFormat("#0.0");
                String formattedResult = decimalFormat.format(result);
                finalScore.put(movieId,Double.parseDouble(formattedResult));
            }
        }
        System.out.println(finalScore);
        list.forEach(movie -> {
            Integer movieId = movie.getId();
            if(finalScore.containsKey(movieId)){
                movie.setScore(finalScore.get(movieId));
            }
        });
        movieService.updateBatchById(list);
        log.info("**************************更新Movie数据库的平均分值成功**************************");
    }
}
