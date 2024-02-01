package com.lc.project.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
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
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.crypto.spec.PSource;
import java.io.*;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static com.lc.project.constant.CommonConstant.*;
import static com.lc.project.websocket.ChatHandler.threadPoolExecutor;

/**
 * @author asus
 * @description 针对表【video_upload】的数据库操作Service实现
 * @createDate 2023-12-29 19:12:55
 */
@Service
public class VideoUploadServiceImpl extends ServiceImpl<VideoUploadMapper, VideoUpload>
        implements VideoUploadService {


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
        if (!mediaPath.exists()) {
            mediaPath.mkdirs();
            if (!mediaPath.mkdirs()) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件上传失败，无法创建文件夹");
            }
        }
        //原始文件名
        String originFileName = file.getOriginalFilename();
        //后缀
        String fileSuffix = originFileName.substring(originFileName.lastIndexOf(".") + 1);
        //新文件名
        String nameWithOutSuffix = UUID.randomUUID().toString().replace("-", "");
        //带后缀的新文件名
        String newFileName = nameWithOutSuffix + "." + fileSuffix;
        //上传
        File descFile = new File(mediaPath.getAbsoluteFile(), newFileName);
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
    public Integer uploadVideoToM3U8(MultipartFile file) {
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

        CompletableFuture.runAsync(() -> {
            //m3u8分片编码
            // 使用FFmpeg将视频分成多个小块

            //被注释的这个快一点
            //ProcessBuilder pb = new ProcessBuilder("ffmpeg", "-i", video.getPath(), "-codec", "copy", "-start_number", "0", "-hls_time", "1", "-hls_list_size", "0", "-f", "hls", m3u8.getPath());
            ProcessBuilder pb = new ProcessBuilder("ffmpeg", "-i", video.getPath(), "-force_key_frames", "expr:gte(t,n_forced*1)", "-strict", "-2", "-c:a", "aac", "-c:v", "libx264", "-hls_time", "1", "-hls_list_size", "0", "-f", "hls", m3u8.getPath());
//        ProcessBuilder pb2 = new ProcessBuilder("ffmpeg", "-i", video.getPath(), "-ss 00:00:00 -to 00:06:00", "-force_key_frames", "expr:gte(t,n_forced*1)", "-strict", "-2", "-c:a", "aac","-c:v", "libx264", "-hls_time", "1", "-hls_list_size", "0", "-f", "hls", m3u8.getPath());
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
        }, threadPoolExecutor);
        String tempUrl = SERVER_PATH + newFileName.replace("\\", "/");
        return null;
    }


    @Override
    public Integer uploadVideoToM3U82(MultipartFile file) {
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
        CompletableFuture.runAsync(() -> {
            ProcessBuilder pb = new ProcessBuilder("ffmpeg", "-i", video.getPath(),
                    "-ss", "00:00", "-to", "06:00",
                    "-force_key_frames",
                    "expr:gte(t,n_forced*1)",
                    "-strict", "-2",
                    "-c:a",
                    "aac",
                    "-c:v",
                    "libx264",
                    "-hls_time",
                    "1",
                    "-hls_list_size",
                    "0",
                    "-f",
                    "hls",
                    m3u8.getPath());
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
        }, threadPoolExecutor);
        //被注释的这个快一点
        //ProcessBuilder pb = new ProcessBuilder("ffmpeg", "-i", video.getPath(), "-codec", "copy", "-start_number", "0", "-hls_time", "1", "-hls_list_size", "0", "-f", "hls", m3u8.getPath());
//        ProcessBuilder pb = new ProcessBuilder("ffmpeg", "-i", video.getPath(), "-force_key_frames", "expr:gte(t,n_forced*1)", "-strict", "-2", "-c:a", "aac","-c:v", "libx264", "-hls_time", "1", "-hls_list_size", "0", "-f", "hls", m3u8.getPath());

//        /api/videoSystem/file/video/20240127/098f948f1df14ac386bcef108b4f51b9/index.m3u8
        String tempUrl = SERVER_PATH + newFileName.replace("\\", "/");
        return null;
    }

    @Override
    public VideoUpload getVideoById(Integer videoId, Integer movieState, Integer movieId) {
        Users loginUser = usersService.getLoginUser();
        VideoUpload videoUpload = videoUploadMapper.selectById(videoId);
        //如果用户没有登录
        if (loginUser == null) {
            //如果电影不需要购买
            if (movieState == 1) {
                return videoUpload;
            }
            String videoSixUrl = videoUpload.getVideoSixUrl();
            videoUpload.setVideoUrl(videoSixUrl);
            return videoUpload;
        }
        String currentUserId = loginUser.getId();
        //如果电影需要购买
        if (movieState.equals(3)) {

            QueryWrapper<Purchased> purchasedQueryWrapper = new QueryWrapper<>();
            purchasedQueryWrapper.eq("userId", currentUserId);
            purchasedQueryWrapper.eq("movieId", movieId);
            long count = purchasedService.count(purchasedQueryWrapper);
            //如果没有购买电影
            if (count <= 0) {
                String videoSixUrl = videoUpload.getVideoSixUrl();
                videoUpload.setVideoUrl(videoSixUrl);
                return videoUpload;
            }
        }

        //如果电影需要会员
        if (movieState.equals(2)) {
            QueryWrapper<Vip> vipQueryWrapper = new QueryWrapper<>();
            vipQueryWrapper.eq("userId", currentUserId);
            Vip vip = vipMapper.selectOne(vipQueryWrapper);
            if (vip == null) {
                String videoSixUrl = videoUpload.getVideoSixUrl();
                videoUpload.setVideoUrl(videoSixUrl);
                return videoUpload;
            }
            Date overTime = vip.getOverTime();
            Date date = new Date();
            //如果已经过期
            if (date.after(overTime)) {
                String videoSixUrl = videoUpload.getVideoSixUrl();
                videoUpload.setVideoUrl(videoSixUrl);
                return videoUpload;
            }
        }

        return videoUpload;
    }


    @Override
    public Integer uploadVideoToM3U83(MultipartFile file) {
        // 获取上传资源的名称
        String filename = file.getOriginalFilename();
        QueryWrapper<VideoUpload> videoUploadQueryWrapper = new QueryWrapper<>();
        videoUploadQueryWrapper.eq("videoName",filename);
        VideoUpload oldVideoUpload = videoUploadMapper.selectOne(videoUploadQueryWrapper);
        if(oldVideoUpload != null){
            return oldVideoUpload.getId();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String date = sdf.format(new Date());

        // 为了保证图片在服务器中名字的唯一性，使用UUID来对filename进行改写 6分钟 uuid
        String sixUuid = UUID.randomUUID().toString().replace("-", "");
        // 将生成的uuid和filename惊醒拼接
        String newFileName = "video" + File.separator + date + File.separator + sixUuid + File.separator + filename;
        // 创建一个文件实例对象
        File sixVideo = new File(basepath, newFileName);

        if (!sixVideo.getParentFile().exists()) {
            sixVideo.getParentFile().mkdirs();
        }

        // 对这个文件进行上传操作
        try {
            FileUtils.copyInputStreamToFile(file.getInputStream(),sixVideo);
//              IoUtil.copy(file.getInputStream(), Files.newOutputStream(sixVideo.toPath()));
//            file.transferTo(sixVideo);
        } catch (IOException e) {
            e.printStackTrace();
        }
        File m3u8 = new File(basepath + File.separator + newFileName.substring(0, newFileName.lastIndexOf(".")) + ".m3u8");
        try {
            m3u8.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //所有的视频的uuid
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String allNewFileName = "video" + File.separator + date + File.separator + uuid + File.separator + filename;
        // 创建一个文件实例对象
        File allVideo = new File(basepath, allNewFileName);

        if (!allVideo.getParentFile().exists()) {
            allVideo.getParentFile().mkdirs();
        }
        // 对这个文件进行上传操作
        try {
//            IoUtil.copy(file.getInputStream(), Files.newOutputStream(allVideo.toPath()));
            FileUtils.copyInputStreamToFile(file.getInputStream(),allVideo);
//            file.transferTo(allVideo);
        } catch (IOException e) {
            e.printStackTrace();
        }
        File Allm3u8 = new File(basepath + File.separator + allNewFileName.substring(0, allNewFileName.lastIndexOf(".")) + ".m3u8");
        try {
            Allm3u8.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }


//        System.out.println(m3u8.getPath());
        String output1 = basepath + File.separator + "video" + File.separator + date + File.separator + sixUuid + File.separator + "meeting_01_%v.m3u8";
        String output0 = basepath + File.separator + "video" + File.separator + date + File.separator + sixUuid + File.separator + "meeting_01_%v-%09d.ts";
        String allOutput1 = basepath + File.separator + "video" + File.separator + date + File.separator + uuid + File.separator + "meeting_01_%v.m3u8";
        String allOutput0 = basepath + File.separator + "video" + File.separator + date + File.separator + uuid + File.separator + "meeting_01_%v-%09d.ts";
        CompletableFuture.runAsync(() -> {
             uploadM3u8Six(sixVideo,output0,output1);
        }, threadPoolExecutor);

        CompletableFuture.runAsync(() -> {
            uploadM3u8All(allVideo,allOutput0,allOutput1);
        }, threadPoolExecutor);

        VideoUpload videoUpload = new VideoUpload();
        videoUpload.setVideoName(filename);
        String six = "/api/videoSystem/file/" + "video" + File.separator + date + File.separator + sixUuid + File.separator + "index.m3u8";
        String all = "/api/videoSystem/file/" + "video" + File.separator + date + File.separator + uuid + File.separator + "index.m3u8";
        videoUpload.setVideoSixUrl(six);
        videoUpload.setVideoUrl(all);
        videoUpload.setVideoUUID(uuid);
        boolean save = this.save(videoUpload);
        return videoUpload.getId();
    }

    public void uploadM3u8Six(File video, String sixOut0, String sixOut1) {
        ProcessBuilder pb = new ProcessBuilder("ffmpeg", "-threads", "2", "-re", "-fflags", "+genpts", "-i", video.getPath(), "-ss", "00:00", "-to", "06:00",
                "-s:0", "1920x1080", "-ac", "2", "-vcodec", "libx264", "-profile:v", "main", "-b:v:0", "2000k", "-maxrate:0", "2000k", "-bufsize:0", "4000k", "-r", "30", "-ar", "44100", "-g", "48", "-c:a", "aac", "-b:a:0", "128k",
                "-s:2", "1280x720", "-ac", "2", "-vcodec", "libx264", "-profile:v", "main", "-b:v:1", "1000k", "-maxrate:2", "1000k", "-bufsize:2", "2000k", "-r", "30", "-ar", "44100", "-g", "48", "-c:a", "aac", "-b:a:1", "128k",
                "-s:4", "720x480", "-ac", "2", "-vcodec", "libx264", "-profile:v", "main", "-b:v:2", "600k", "-maxrate:4", "600k", "-bufsize:4", "1000k", "-r", "30", "-ar", "44100", "-g", "48", "-c:a", "aac", "-b:a:2", "128k",
//                -s:4 720x480 -ac 2 -vcodec libx264 -profile:v main -b:v:2 600k -maxrate:4 600k -bufsize:4 1000k -r 30 -ar 44100 -g 48 -c:a aac -b:a:2 128k
                "-map", "0:v", "-map", "0:a", "-map", "0:v", "-map", "0:a", "-map", "0:v", "-map", "0:a", "-f", "hls", "-var_stream_map", "v:0,a:0 v:1,a:1 v:2,a:2",
                "-start_number", "10", "-hls_time", "10", "-hls_list_size", "0", "-hls_start_number_source", "1", "-master_pl_name", "index.m3u8", "-hls_segment_filename",
                sixOut0, sixOut1);
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
    }


    public void uploadM3u8All(File video, String allOut0, String allOut1) {
        ProcessBuilder pb = new ProcessBuilder("ffmpeg", "-threads", "2", "-re", "-fflags", "+genpts", "-i", video.getPath(),
                "-s:0", "1920x1080", "-ac", "2", "-vcodec", "libx264", "-profile:v", "main", "-b:v:0", "2000k", "-maxrate:0", "2000k", "-bufsize:0", "4000k", "-r", "30", "-ar", "44100", "-g", "48", "-c:a", "aac", "-b:a:0", "128k",
                "-s:2", "1280x720", "-ac", "2", "-vcodec", "libx264", "-profile:v", "main", "-b:v:1", "1000k", "-maxrate:2", "1000k", "-bufsize:2", "2000k", "-r", "30", "-ar", "44100", "-g", "48", "-c:a", "aac", "-b:a:1", "128k",
                "-s:4", "720x480", "-ac", "2", "-vcodec", "libx264", "-profile:v", "main", "-b:v:2", "600k", "-maxrate:4", "600k", "-bufsize:4", "1000k", "-r", "30", "-ar", "44100", "-g", "48", "-c:a", "aac", "-b:a:2", "128k",
//                -s:4 720x480 -ac 2 -vcodec libx264 -profile:v main -b:v:2 600k -maxrate:4 600k -bufsize:4 1000k -r 30 -ar 44100 -g 48 -c:a aac -b:a:2 128k
                "-map", "0:v", "-map", "0:a", "-map", "0:v", "-map", "0:a", "-map", "0:v", "-map", "0:a", "-f", "hls", "-var_stream_map", "v:0,a:0 v:1,a:1 v:2,a:2",
                "-start_number", "10", "-hls_time", "10", "-hls_list_size", "0", "-hls_start_number_source", "1", "-master_pl_name", "index.m3u8", "-hls_segment_filename",
                allOut0, allOut1);
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
    }
}




