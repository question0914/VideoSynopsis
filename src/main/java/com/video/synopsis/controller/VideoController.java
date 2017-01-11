package com.video.synopsis.controller;

import com.video.synopsis.domain.UploadedFile;
import com.video.synopsis.domain.VideoFilesDao;
import com.video.synopsis.service.IVideoService;
import com.video.synopsis.utils.PropUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


@Controller
@RequestMapping("/video")
public class VideoController {

    private static final Logger _log = LoggerFactory.getLogger(VideoController.class);
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    @Autowired
    private IVideoService videoService;

    private UploadedFile uploadedFile;

    @Autowired
    private PropUtils propUtils;

    public VideoController() {
        System.out.println("init RestController");
        uploadedFile = new UploadedFile();
    }

    @RequestMapping(value = "/get/{value:.+}", method = RequestMethod.GET)
    public void get(HttpServletRequest request, HttpServletResponse response,
                    @PathVariable String value) {
        try {
            String fullPath = propUtils.getProperty("result.video.path").concat("/").concat(value);
            File file = new File(fullPath);
            FileInputStream in = new FileInputStream(file);
            byte[] content = new byte[(int) file.length()];
            in.read(content);
            ServletContext sc = request.getSession().getServletContext();
            String mimeType = sc.getMimeType(file.getName());
            response.reset();
            response.setContentType(mimeType);
            response.setContentLength(content.length);
            response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
            org.springframework.util.FileCopyUtils.copy(content, response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public String upload(MultipartHttpServletRequest request, HttpServletResponse response) {

        //0. notice, we have used MultipartHttpServletRequest

        //1. get the files from the request object
        Iterator<String> itr = request.getFileNames();
        MultipartFile mpf = request.getFile(itr.next());
        _log.debug(mpf.getOriginalFilename() + " uploaded!");


        uploadedFile.type = mpf.getContentType();


        videoService.videoSynopsis(mpf);

        return "[{\"result\":\"success\", \"uploadTime\":\"" + sdf.format(new Date()) + "\"}]";
    }

    @RequestMapping(value = "/synopsis", method = RequestMethod.POST)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    public String singleIp(String filePath) {
        _log.debug("Arrival filePath:\t" + filePath);
        return "[{\"result\":\"success\", \"uploadTime\":\"" + sdf.format(new Date()) + "\"}]";
    }

    @RequestMapping(value = "/files", method = RequestMethod.GET, produces = "text/plain")
    @ResponseBody
    public List<VideoFilesDao> displayFiles(HttpServletRequest request, Model model) {

        return videoService.displayResultFile();
    }
}
