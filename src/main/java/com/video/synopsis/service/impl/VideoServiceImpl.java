package com.video.synopsis.service.impl;

import com.video.synopsis.domain.VideoFilesDao;
import com.video.synopsis.process.VideoSynopsis;
import com.video.synopsis.service.IVideoService;
import com.video.synopsis.utils.FileUtils;
import com.video.synopsis.utils.PropUtils;
import com.video.synopsis.utils.StrUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;


@Service("videoService")
public class VideoServiceImpl implements IVideoService {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private PropUtils propUtils;

    @Autowired
    private FileUtils fileUtils;

    @Autowired
    private VideoSynopsis VideoSynopsis;

    @Override
    public boolean videoSynopsis(MultipartFile mpf) {
        try {
            File video = fileUtils.stream2file(mpf.getInputStream());
            String videoPath = video.getAbsolutePath();
            VideoSynopsis.process(videoPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public List<VideoFilesDao> displayResultFile() {
        String videoPath = propUtils.getProperty("result.video.path");
        if (StrUtils.isEmpty(videoPath))
            return null;
        File resultPath = new File(videoPath);
        if (!resultPath.exists() || !resultPath.isDirectory()) {
            return null;
        }
        List<VideoFilesDao> videoFilesDaoList = new LinkedList<>();
        VideoFilesDao videoFilesDao;
        for (File video : resultPath.listFiles()) {
            videoFilesDao = new VideoFilesDao();
            String name = video.getName();
            videoFilesDao.setFileName(name);
            videoFilesDao.setModifyDate(sdf.format(new Date(video.lastModified())));
            videoFilesDao.setDownloadLink("http://localhost:8080/video/get/".concat(name));
            videoFilesDaoList.add(videoFilesDao);
        }
        return videoFilesDaoList;
    }
}
