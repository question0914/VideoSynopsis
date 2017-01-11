package com.video.synopsis.process;

import com.video.synopsis.utils.PropUtils;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_highgui;
import org.bytedeco.javacpp.opencv_highgui.CvVideoWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class ImageToVideo {

    private static final Logger _log = LoggerFactory.getLogger(ImageToVideo.class);

    private static Map<String, File> imageMap = new HashMap<>();
    private static Double fps = 120.0;

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss_SSS");

    @Autowired
    private PropUtils propUtils;

    public String generatedAimVideo(String imagePath, String backgroundPath, String videoPath) {
        BufferedImage bufferedImage;
        try {
            _log.info("Start read background jpg...");
            bufferedImage = ImageIO.read(new File(backgroundPath + "background.jpg"));
            _log.info("End read background jpg...");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();


        BackgroundSubtraction.mkDir(new File(videoPath));
        String resultPath = videoPath.concat("/").
                concat(String.format("VideoSynopsis%s.avi", sdf.format(new Date())));
        //CvVideoWriter vw used to generate condensed video clip to result path
        CvVideoWriter vw = opencv_highgui.cvCreateVideoWriter(resultPath,
                opencv_highgui.CV_FOURCC((byte) 'D', (byte) 'I', (byte) 'V', (byte) 'X'), fps, opencv_core.cvSize(width, height));
        File folder = new File(imagePath);
        File[] listOfFiles = folder.listFiles();
        for (File file : listOfFiles) {
            if (file.isFile()) {
                _log.info("file.getName() :" + file.getName());
                imageMap.put(file.getName(), file);
            }
        }

        IplImage img;
        for (int index = 1; index < listOfFiles.length; index++) {
            BufferedImage screen = getImage(index);
            BufferedImage bgrScreen = convertToType(screen, BufferedImage.TYPE_3BYTE_BGR);

            img = IplImage.createFrom(bgrScreen);
            opencv_highgui.cvWriteFrame(vw, img);//write images into the video file
        }
        opencv_highgui.cvReleaseVideoWriter(vw);
        _log.info("Done");
        return resultPath;
    }

    public static BufferedImage convertToType(BufferedImage sourceImage, int targetType) {
        BufferedImage image;
        if (sourceImage.getType() == targetType) {
            image = sourceImage;
        } else {
            image = new BufferedImage(sourceImage.getWidth(),
                    sourceImage.getHeight(), targetType);
            image.getGraphics().drawImage(sourceImage, 0, 0, null);
        }
        return image;
    }

    private static BufferedImage getImage(int index) {

        try {
            String fileName = index + ".jpg";
            _log.info("fileName :" + fileName);
            File img = imageMap.get(fileName);
            BufferedImage in;
            if (img != null) {
                _log.info("img :" + img.getName());
                in = ImageIO.read(img);
            } else {
                _log.info("index :" + index);
                img = imageMap.get(1);
                in = ImageIO.read(img);
            }
            return in;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}