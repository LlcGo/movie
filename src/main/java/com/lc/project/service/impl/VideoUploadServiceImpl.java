package com.lc.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lc.project.common.ErrorCode;
import com.lc.project.exception.BusinessException;
import com.lc.project.mapper.VideoUploadMapper;
import com.lc.project.mapper.VipMapper;
import com.lc.project.model.entity.Purchased;
import com.lc.project.model.entity.Users;
import com.lc.project.model.entity.VideoUpload;
import com.lc.project.model.entity.Vip;
import com.lc.project.service.PurchasedService;
import com.lc.project.service.UsersService;
import com.lc.project.service.VideoUploadService;
import com.lc.project.service.VipService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import static com.lc.project.constant.CommonConstant.*;

/**
* @author asus
* @description 针对表【video_upload】的数据库操作Service实现
* @createDate 2023-12-29 19:12:55
*/
@Service
public class VideoUploadServiceImpl extends ServiceImpl<VideoUploadMapper, VideoUpload>
    implements VideoUploadService{


    // 定义一个目标路径，就是我们要把图片上传的位置
    @Value(value = "${llg.path.upload}")
    private String basepath;

    // 定义访问图片路径
    @Value(value = "${llg.path.filePath}")
    private String SERVER_PATH;

    @Resource
    private VideoUploadMapper videoUploadMapper;

    @Resource
    private PurchasedService purchasedService;

    @Resource
    private UsersService usersService;

    @Resource
    private VipMapper vipMapper;

    @Override
    public String uploadUserImg(MultipartFile file) {
        File mediaPath = new File(UPLOADS_IMG_PATH);
        if(!mediaPath.exists()){
            mediaPath.mkdirs();
            if(!mediaPath.mkdirs()){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"文件上传失败，无法创建文件夹");
            }
        }
        //原始文件名
        String originFileName = file.getOriginalFilename();
        //后缀
        String fileSuffix = originFileName.substring(originFileName.lastIndexOf(".")+1);
        //新文件名
        String nameWithOutSuffix = UUID.randomUUID().toString().replace("-", "");
        //带后缀的新文件名
        String newFileName = nameWithOutSuffix+"."+fileSuffix;
        //上传
        File descFile = new File(mediaPath.getAbsoluteFile(),newFileName);
        try {
            file.transferTo(descFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String intoSqlUrI = "/api/uploads/img/" + newFileName;

        return intoSqlUrI;
    }

    @Override
    public String uploadImg(MultipartFile file) {
        String contentType = file.getContentType();
        // 获取上传图片的名称
        String filename = file.getOriginalFilename();
        // 为了保证图片在服务器中名字的唯一性，使用UUID来对filename进行改写
        String uuid = UUID.randomUUID().toString().replace("-", "");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String date = sdf.format(new Date());
        // 将生成的uuid和filename惊醒拼接
        String newFileName = "img" + File.separator + date + File.separator + uuid + '-' + filename;
        // 创建一个文件实例对象
        File image = new File(basepath, newFileName);

        if (!image.exists()) {
            image.mkdirs();
        }
        // 对这个文件进行上传操作
        try {
            file.transferTo(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(SERVER_PATH);

        return SERVER_PATH + newFileName.replace("\\", "/");
    }

    @Override
    public String uploadVideoToM3U8(MultipartFile file) {
        String contentType = file.getContentType();
        // 获取上传图片的名称
        String filename = file.getOriginalFilename();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String date = sdf.format(new Date());

        // 为了保证图片在服务器中名字的唯一性，使用UUID来对filename进行改写
        String uuid = UUID.randomUUID().toString().replace("-", "");
        // 将生成的uuid和filename惊醒拼接
        String newFileName = "video" + File.separator + date + File.separator + uuid + File.separator + filename;
        // 创建一个文件实例对象
        File video = new File(basepath, newFileName);

        if (!video.exists()) {
            video.mkdirs();
        }
        // 对这个文件进行上传操作
        try {
            file.transferTo(video);
        } catch (IOException e) {
            e.printStackTrace();
        }
        File m3u8 = new File(basepath + File.separator + newFileName.substring(0, newFileName.lastIndexOf(".")) + ".m3u8");
        try {
            m3u8.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //m3u8分片编码
        // 使用FFmpeg将视频分成多个小块

        //被注释的这个快一点
        //ProcessBuilder pb = new ProcessBuilder("ffmpeg", "-i", video.getPath(), "-codec", "copy", "-start_number", "0", "-hls_time", "1", "-hls_list_size", "0", "-f", "hls", m3u8.getPath());
        ProcessBuilder pb = new ProcessBuilder("ffmpeg", "-i", video.getPath(), "-force_key_frames", "expr:gte(t,n_forced*1)", "-strict", "-2", "-c:a", "aac","-c:v", "libx264", "-hls_time", "1", "-hls_list_size", "0", "-f", "hls", m3u8.getPath());
        ProcessBuilder pb2 = new ProcessBuilder("ffmpeg", "-i", video.getPath(), "-ss 00:00:00 -to 00:06:00", "-force_key_frames", "expr:gte(t,n_forced*1)", "-strict", "-2", "-c:a", "aac","-c:v", "libx264", "-hls_time", "1", "-hls_list_size", "0", "-f", "hls", m3u8.getPath());
        // 将标准错误流和标准输出流合并
        pb2.redirectErrorStream(true);
        pb.redirectErrorStream(true);

        Process p = null;
        try {
            p = pb.start();

            // 处理FFmpeg的输出信息
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            p.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String tempUrl = SERVER_PATH + newFileName.replace("\\", "/");
        return tempUrl.substring(0, tempUrl.lastIndexOf(".")) + ".m3u8";
    }


    @Override
    public String uploadVideoToM3U82(MultipartFile file) {
        String contentType = file.getContentType();
        // 获取上传图片的名称
        String filename = file.getOriginalFilename();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String date = sdf.format(new Date());

        // 为了保证图片在服务器中名字的唯一性，使用UUID来对filename进行改写
        String uuid = UUID.randomUUID().toString().replace("-", "");
        // 将生成的uuid和filename惊醒拼接
        String newFileName = "video" + File.separator + date + File.separator + uuid + File.separator + filename;
        // 创建一个文件实例对象
        File video = new File(basepath, newFileName);

        if (!video.exists()) {
            video.mkdirs();
        }
        // 对这个文件进行上传操作
        try {
            file.transferTo(video);
        } catch (IOException e) {
            e.printStackTrace();
        }
        File m3u8 = new File(basepath + File.separator + newFileName.substring(0, newFileName.lastIndexOf(".")) + ".m3u8");
        try {
            m3u8.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //m3u8分片编码
        // 使用FFmpeg将视频分成多个小块

        //被注释的这个快一点
        //ProcessBuilder pb = new ProcessBuilder("ffmpeg", "-i", video.getPath(), "-codec", "copy", "-start_number", "0", "-hls_time", "1", "-hls_list_size", "0", "-f", "hls", m3u8.getPath());
//        ProcessBuilder pb = new ProcessBuilder("ffmpeg", "-i", video.getPath(), "-force_key_frames", "expr:gte(t,n_forced*1)", "-strict", "-2", "-c:a", "aac","-c:v", "libx264", "-hls_time", "1", "-hls_list_size", "0", "-f", "hls", m3u8.getPath());
        ProcessBuilder pb = new ProcessBuilder("ffmpeg", "-i", video.getPath(), "-ss", "00:00" ,"-to", "06:00","-force_key_frames", "expr:gte(t,n_forced*1)", "-strict", "-2", "-c:a", "aac","-c:v", "libx264", "-hls_time", "1", "-hls_list_size", "0", "-f", "hls", m3u8.getPath());
        // 将标准错误流和标准输出流合并
//        pb2.redirectErrorStream(true);
        pb.redirectErrorStream(true);

        Process p = null;
        try {
            p = pb.start();

            // 处理FFmpeg的输出信息
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            p.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String tempUrl = SERVER_PATH + newFileName.replace("\\", "/");
        return tempUrl.substring(0, tempUrl.lastIndexOf(".")) + ".m3u8";
    }

    @Override
    public VideoUpload getVideoById(Integer videoId,Integer movieState,Integer movieId) {
        Users loginUser = usersService.getLoginUser();
        VideoUpload videoUpload = videoUploadMapper.selectById(videoId);
        if (loginUser == null){
            String videoSixUrl = videoUpload.getVideoSixUrl();
            videoUpload.setVideoUrl(videoSixUrl);
            return videoUpload;
        }
        String currentUserId = loginUser.getId();
        //如果电影需要购买
        if(movieState.equals(3)){

            QueryWrapper<Purchased> purchasedQueryWrapper = new QueryWrapper<>();
            purchasedQueryWrapper.eq("userId",currentUserId);
            purchasedQueryWrapper.eq("movieId",movieId);
            long count = purchasedService.count(purchasedQueryWrapper);
            //如果没有购买电影
            if(count < 0){
                String videoSixUrl = videoUpload.getVideoSixUrl();
                videoUpload.setVideoUrl(videoSixUrl);
                return videoUpload;
            }
        }

        //如果电影需要会员
        if (movieState.equals(2)){
            QueryWrapper<Vip> vipQueryWrapper = new QueryWrapper<>();
            vipQueryWrapper.eq("userId",currentUserId);
            Vip vip = vipMapper.selectOne(vipQueryWrapper);
            Date overTime = vip.getOverTime();
            Date date = new Date();
            //如果已经过期
            if(date.after(overTime)){
                String videoSixUrl = videoUpload.getVideoSixUrl();
                videoUpload.setVideoUrl(videoSixUrl);
                return videoUpload;
            }
        }

        return videoUpload;
    }

}




