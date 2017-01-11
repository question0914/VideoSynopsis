package com.video.synopsis.service;

import com.video.synopsis.domain.VideoFilesDao;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface IVideoService {

    boolean videoSynopsis(MultipartFile mpf);

    List<VideoFilesDao> displayResultFile();
}
