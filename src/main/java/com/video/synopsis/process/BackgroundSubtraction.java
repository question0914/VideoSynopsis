package com.video.synopsis.process;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.*;
import org.bytedeco.javacpp.opencv_highgui;
import org.bytedeco.javacpp.opencv_imgproc;
import org.bytedeco.javacpp.opencv_video.BackgroundSubtractorMOG2;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class BackgroundSubtraction {

    private static final Logger _log = LoggerFactory.getLogger(BackgroundSubtraction.class);

    public void getVideoFrames(String videoName, String imagePath, String backgroundPath) {
        _log.info("Starting getFrames...");
        _log.info("Video is " + videoName);
        if (videoName == null) {
            _log.info("Source video is null.");
            return;
        }
        File file = new File(videoName);
        if (!file.exists() || file.isDirectory()) {
            _log.info("Source video is not exist.");
            return;
        }
        FrameGrabber grabber = new OpenCVFrameGrabber(file);
        IplImage grabbedImage;
        try {
            grabber.start();
            _log.info("FrameGrabber is started.");
            grabbedImage = grabber.grab();
        } catch (Exception e) {
            e.printStackTrace();
            closeGrabber(grabber);
            return;
        }

        IplImage foreground = null;
        Mat background = new Mat();
        IplImage bgImage;
        BackgroundSubtractorMOG2 mog = new BackgroundSubtractorMOG2(30, 40, false);
        IplImage frame = grabbedImage.clone();
        CvSeq contour = new CvSeq();
        CvSeq ptr;
        CvMemStorage storage = CvMemStorage.create();
        int counter = 1;
        _log.info("ImagePath is " + imagePath);
        File imageDir = new File(imagePath);
        mkDir(imageDir);

        _log.info("Starting deal with Frame...");
        while (grabbedImage != null) {

            if (foreground == null) {
                foreground = IplImage.create(frame.width(), frame.height(), opencv_core.IPL_DEPTH_8U, 1);
                _log.info("Frame width: " + frame.width());
            }
            mog.apply(new Mat(grabbedImage), new Mat(foreground), -1);//Computes a foreground mask, output binary image

            opencv_imgproc.cvDilate(foreground, foreground, null, 5);
            opencv_imgproc.cvErode(foreground, foreground, null, 6);
            opencv_imgproc.cvSmooth(foreground, foreground, opencv_imgproc.CV_MEDIAN, 3, 3, 2, 2);
            opencv_imgproc.cvFindContours(foreground, storage, contour,
                    Loader.sizeof(CvContour.class), opencv_imgproc.CV_RETR_LIST,
                    opencv_imgproc.CV_CHAIN_APPROX_SIMPLE);
            for (ptr = contour; ptr != null; ptr = ptr.h_next()) {
                if (ptr.address() == 0) {
                    break;
                }
                CvRect boundBox = opencv_imgproc.cvBoundingRect(ptr, 0);
                if (boundBox.width() * boundBox.height() > 900) {
                    _log.info("x : " + boundBox.x() + " ~ " + (boundBox.x() + boundBox.width() + 1) + " y : " +
                            boundBox.y() + " ~ " + (boundBox.y() + boundBox.height() + 1));

                    opencv_core.cvRectangle(grabbedImage,
                            opencv_core.cvPoint(boundBox.x(), boundBox.y()),
                            opencv_core.cvPoint(boundBox.x() + boundBox.width(), boundBox.y() + boundBox.height()),
                            opencv_core.CV_RGB(255, 0, 0), 1, 8, 0);
                    CvFont font = new CvFont();
                    opencv_core.cvPutText(grabbedImage, "0",
                            opencv_core.cvPoint(boundBox.x(), boundBox.y()), font, opencv_core.CvScalar.RED);


                    _log.debug("save picture into  ..." + imagePath.concat("/").concat(counter + ".jpg"));
                    opencv_highgui.cvSaveImage(imagePath.concat("/").concat(counter + ".jpg"), grabbedImage);
                    counter++;
                }
            }
            try {
                grabbedImage = grabber.grab();
            } catch (Exception e) {
                closeGrabber(grabber);
                e.printStackTrace();
                break;
            }
        }
        _log.info("Ending deal with Frame...");
        mog.getBackgroundImage(background);
        bgImage = background.asIplImage();
        _log.info("Background Path is  " + backgroundPath);
        File backgroundDir = new File(backgroundPath);
        mkDir(backgroundDir);
        _log.info("Starting save background jpg...");
        _log.info("Save background.jpg into " + backgroundPath + "background.jpg");
        opencv_highgui.cvSaveImage(backgroundPath + "background.jpg", bgImage);
        _log.info("Ending save background jpg...");
        try {
            grabber.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        _log.info("End getFrame.");
    }

    public static void mkDir(File backgroundDir) {
        if (backgroundDir.exists()) {
            _log.info("BackgroundDir is existed, deleting...");
            backgroundDir.deleteOnExit();
        }
        _log.info("Make new backgroundDir.");
        backgroundDir.mkdir();
    }

    private void closeGrabber(FrameGrabber grabber) {
        if (grabber != null) {
            try {
                grabber.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
