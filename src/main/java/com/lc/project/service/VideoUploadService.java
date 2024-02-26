package com.lc.project.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lc.project.model.dto.file.VideQueryRequest;
import com.lc.project.model.entity.VideoUpload;
import org.springframework.web.multipart.MultipartFile;

/**
* @author asus
* @description 针对表【video_upload】的数据库操作Service
* @createDate 2023-12-29 19:12:55
*/
public interface VideoUploadService extends IService<VideoUpload> {

    String uploadUserImg(MultipartFile file);

    String uploadImg(MultipartFile multipartFile);

    /**
     * 全部直接分片处理不管分辨率
     * @param multipartFile
     * @return
     */
    Integer uploadVideoToM3U8(MultipartFile multipartFile);

    /**
     * 只分片6分钟
     * @param file
     * @return
     */
    Integer uploadVideoToM3U82(MultipartFile file);


    VideoUpload getVideoById(Integer videoId,Integer movieState,Integer movieId);

    /**
     * 将视频编码为3中清晰度
     * @param file
     * @return
     */
    Integer uploadVideoToM3U83(MultipartFile file);

    Page<VideoUpload> getListVideo(VideQueryRequest videQueryRequest);
}
