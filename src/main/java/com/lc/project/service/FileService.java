package com.lc.project.service;

import com.lc.project.model.dto.file.FileChunk;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    Boolean uploadFile(FileChunk fileChunk, MultipartFile file);
}
