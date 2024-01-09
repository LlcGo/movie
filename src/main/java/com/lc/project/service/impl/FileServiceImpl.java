package com.lc.project.service.impl;

import com.lc.project.model.dto.file.FileChunk;
import com.lc.project.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.RandomAccessFile;

import static com.lc.project.constant.CommonConstant.UPLOADS_PATH;

@Service
@Slf4j
public class FileServiceImpl implements FileService {

    /**
     * 默认分块大小
     */
    @Value("${file.chunk-size}")
    public Long defaultChunkSize;


    @Override
    public Boolean uploadFile(FileChunk fileChunk, MultipartFile file) {
        String filename = fileChunk.getFilename();
        String output = UPLOADS_PATH  + filename;
        // try 自动资源管理
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(output, "rw")) {
            // 分片大小必须和前端匹配，否则上传会导致文件损坏
            long chunkSize = fileChunk.getChunkSize() == 0L ? defaultChunkSize : fileChunk.getChunkSize().longValue();
            // 偏移量, 意思是我从拿一个位置开始往文件写入，每一片的大小 * 已经存的块数
            long offset = chunkSize * (fileChunk.getChunkNumber());
            // 定位到该分片的偏移量
            randomAccessFile.seek(offset);
            // 写入
            randomAccessFile.write(file.getBytes());
        } catch (IOException e) {
            log.error("文件上传失败：" + e);
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
}
