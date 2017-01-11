package com.video.synopsis.utils;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class PropUtils {

    private static final Logger _log = LoggerFactory.getLogger(PropUtils.class);
    private Properties properties;

    private static final String confPath = "conf/Prop.properties";

    public void init() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream input = classLoader.getResourceAsStream(confPath);
//        System.out.println(PropUtils.class.getClassLoader().getResource("/").getPath());
        properties = new Properties();
        try {
            properties.load(input);
        } catch (IOException e) {
            _log.error(e.getMessage());
        } finally {
            if (input != null)
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    public PropUtils() {
    }

    public String getProperty(String key) {
        if (properties == null)
            throw new RuntimeException("Properties is not valid!!");
        return properties.getProperty(key);
    }

    public Properties getProperties() {
        return properties;
    }

    @Test
    public void test() {
        PropUtils propUtils = new PropUtils();
        propUtils.init();
        System.out.println(propUtils.getProperties().getProperty("base.path"));
    }
}