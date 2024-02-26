package com.lc.project.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lc.project.common.BaseResponse;
import com.lc.project.common.ResultUtils;
import com.lc.project.model.dto.file.VideQueryRequest;
import com.lc.project.model.entity.VideoUpload;
import com.lc.project.service.VideoUploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/parsing")
public class ParsingController {

    @Resource
    private VideoUploadService videoUploadService;

    @PostMapping("/getList")
    public BaseResponse<Page<VideoUpload>> getListVideo(@RequestBody VideQueryRequest videQueryRequest){
        Page<VideoUpload> videoUploadPage = videoUploadService.getListVideo(videQueryRequest);
        return ResultUtils.success(videoUploadPage);
    }

    @PostMapping("/getVideoList")
    public BaseResponse<List<VideoUpload>> getVideoList(){
        List<VideoUpload> list = videoUploadService.list();
        return ResultUtils.success(list);
    }

}
