package com.lc.project.controller;

import com.lc.project.common.BaseResponse;
import com.lc.project.common.ErrorCode;
import com.lc.project.common.ResultUtils;
import com.lc.project.exception.BusinessException;
import com.lc.project.mapper.VideoUploadMapper;
import com.lc.project.model.dto.file.FileChunk;
import com.lc.project.model.entity.Users;
import com.lc.project.model.entity.VideoUpload;
import com.lc.project.service.FileService;
import com.lc.project.service.UsersService;
import com.lc.project.service.VideoUploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@Slf4j
public class FileController {

    @Resource
    private FileService fileService;

    @Resource
    private VideoUploadMapper videoUploadMapper;

    @Resource
    private VideoUploadService videoUploadService;

    @Resource
    private UsersService usersService;

    @PostMapping("/upload")
    public BaseResponse upload(@RequestParam("file") MultipartFile file,
                               @RequestParam("chunkSize")Long chunkSize,
                               @RequestParam("chunkNumber")Integer chunkNumber,
                               @RequestParam("fileName") String fileName){
        FileChunk fileChunk = new FileChunk();
        fileChunk.setFilename(fileName);
        fileChunk.setChunkSize(chunkSize);
        fileChunk.setChunkNumber(chunkNumber);
        Boolean res = null;
        try {
          res =  fileService.uploadFile(fileChunk,file);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return ResultUtils.success(res);
    }


    @GetMapping("/watchVedio")
    @Deprecated
    public void videoPreview(HttpServletRequest request, HttpServletResponse response, @RequestParam("videoId") Integer videoId) throws Exception {
        VideoUpload videoPathList = videoUploadMapper.selectById(videoId);
        String videoPathUrl = videoPathList.getVideoUrl();
        Path filePath = Paths.get(videoPathUrl);
        if (Files.exists(filePath)) {
            String mimeType = Files.probeContentType(filePath);
            if (StringUtils.hasText(mimeType)) {
                response.setContentType(mimeType);
            }

            // 设置支持部分请求（范围请求）的 'Accept-Ranges' 响应头
            response.setHeader("Accept-Ranges", "bytes");

            // 从请求头中获取请求的视频片段的范围（如果提供）
            long startByte = 0;
            long endByte = Files.size(filePath) - 1;
            String rangeHeader = request.getHeader("Range");
            // System.out.println("rangeHeader:" + rangeHeader);
            if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
                String[] range = rangeHeader.substring(6).split("-");
                startByte = Long.parseLong(range[0]);
                if (range.length == 2) {
                    endByte = Long.parseLong(range[1]);
                }
            }

            // System.out.println("start:" + startByte + ",end:" + endByte);
            log.info("start:" + startByte + ",end:" + endByte);

            // 设置 'Content-Length' 响应头，指示正在发送的视频片段的大小
            long contentLength = endByte - startByte + 1;
            response.setHeader("Content-Length", String.valueOf(contentLength));

            // 设置 'Content-Range' 响应头，指示正在发送的视频片段的范围
            response.setHeader("Content-Range", "bytes " + startByte + "-" + endByte + "/" + Files.size(filePath));

            // 设置响应状态为 '206 Partial Content'
            response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);

            // 使用 'RangeFileChannel' 进行视频片段的传输，以高效地只读取文件的请求部分
            ServletOutputStream outputStream = response.getOutputStream();
            try (RandomAccessFile file = new RandomAccessFile(filePath.toFile(), "r"); FileChannel fileChannel = file.getChannel()) {
                fileChannel.transferTo(startByte, contentLength, Channels.newChannel(outputStream));
            } finally {
                outputStream.close();
            }
        }else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        }
    }

    //获得总体的文件大小 准备为分片上传做准备
    @GetMapping("/getVideoSizeById/{videoId}")
    @Deprecated
    public long getVideoSizeById(@PathVariable("videoId") Integer videoId) throws IOException {
        VideoUpload videoPathList = videoUploadMapper.selectById(videoId);
        String videoPathUrl = videoPathList.getVideoUrl();
        Path filePath = Paths.get(videoPathUrl);
        if (Files.exists(filePath)) {
            return Files.size(filePath);
        }
        return 0L;
    }


    @GetMapping("/getVideo")
    public BaseResponse<VideoUpload> getVideo(Integer videoId,Integer movieState,Integer movieId){
        VideoUpload videoUpload =  videoUploadService.getVideoById(videoId,movieState,movieId);
        return ResultUtils.success(videoUpload);
    }

    @PostMapping("/uploadUserImg")
    public BaseResponse<String> uploadUserImg(@RequestBody MultipartFile file){
        String fileName =  videoUploadService.uploadUserImg(file);
        System.out.println(file);
        return ResultUtils.success(fileName);
    }


    @PostMapping(value = "/file/Img")
    public BaseResponse<String> uploadFileAndImg(@RequestParam("file") MultipartFile multipartFile) {
        return ResultUtils.success(videoUploadService.uploadImg(multipartFile));
    }

    @PostMapping(value = "/uploadVideoToM3U8")
    public BaseResponse<String> uploadVideoToM3U8(@RequestParam("file") MultipartFile multipartFile) {
        return ResultUtils.success(videoUploadService.uploadVideoToM3U82(multipartFile));
    }

}
