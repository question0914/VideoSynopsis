package com.video.synopsis.process;

import com.video.synopsis.utils.PropUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VideoSynopsis {

    private static final Logger _log = LoggerFactory.getLogger(VideoSynopsis.class);

    static {
       System.setProperty("hadoop.home.dir", "C:\\Program Files\\hadoop\\hadoop-2.6.0");
    }

    @Autowired
    private PropUtils propUtils;

    /**
     * @param videoName original video path
     * @return
     */
    public String process(String videoName) {

        SparkConf sparkConf = new SparkConf();
        sparkConf.setAppName("VideoSynopsis");
        sparkConf.setMaster("spark://192.168.1.131:7077");
        String[] jars = new String[1];
        jars[0] = "/home/benedict/Downloads/synopsis.jar";
        sparkConf.setJars(jars);
        JavaSparkContext ctx = new JavaSparkContext(sparkConf);//Instantiating a spark context to start the spark application

        //"C:/video_synopsis2/"
        String basePath = propUtils.getProperty("base.path");
        String backgroundPath = basePath.concat("background/");//background image path
        String imagePath = basePath.concat("images");//frames including moving targets after subtraction
        String videoPath = basePath.concat("video"); //path of processed video

        _log.info(basePath);
        _log.info(videoName);
        _log.info(backgroundPath);
        _log.info(imagePath);
        _log.info(videoPath);


        BackgroundSubtraction bs = new BackgroundSubtraction();
        ImageToVideo it = new ImageToVideo();
        bs.getVideoFrames(videoName, imagePath, backgroundPath);//get frames with moving object
        String resultPath = it.generatedAimVideo(imagePath, backgroundPath, videoPath);//recombine frames into video

        ctx.stop();
        return resultPath;
    }
}