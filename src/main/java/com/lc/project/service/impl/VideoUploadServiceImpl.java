package com.lc.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lc.project.common.ErrorCode;
import com.lc.project.exception.BusinessException;
import com.lc.project.mapper.VideoUploadMapper;
import com.lc.project.model.entity.VideoUpload;
import com.lc.project.service.VideoUploadService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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
}




