package com.lc.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lc.project.model.entity.VideoUpload;
import org.springframework.web.multipart.MultipartFile;

/**
* @author asus
* @description 针对表【video_upload】的数据库操作Service
* @createDate 2023-12-29 19:12:55
*/
public interface VideoUploadService extends IService<VideoUpload> {

    String uploadUserImg(MultipartFile file);
}
