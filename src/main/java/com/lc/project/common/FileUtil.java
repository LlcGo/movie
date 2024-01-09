package com.lc.project.common;

import com.lc.project.constant.CommonConstant;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 文件上传处理工具类
 */
public class FileUtil {

    /**
     * 文件上传
     */
    public static Map<String,String> upload(MultipartFile file) throws Exception{
        Map<String,String> resultMap = new HashMap<>();
        File mediaPath = new File(CommonConstant.UPLOADS_PATH);
        if(!mediaPath.exists()){
            mediaPath.mkdirs();
            if(!mediaPath.mkdirs()){
                throw new Exception("文件上传失败，无法创建文件夹");
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
        file.transferTo(descFile);
        //映射路径
//        String filePath = CommonConstant.UPLOADS_ABSOLUTE_PATH+ newFileName;
//        resultMap.put("fileName", originFileName);
//        resultMap.put("filPath", filePath);
//        resultMap.put("fileSuffix", fileSuffix);


        return resultMap;
    }


}
