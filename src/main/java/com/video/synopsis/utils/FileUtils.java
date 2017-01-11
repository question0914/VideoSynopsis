package com.video.synopsis.utils;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


@Component
public class FileUtils {

    @Autowired
    private PropUtils propUtils;

    public String PREFIX;
    public String SUFFIX;

    public File stream2file(InputStream in) throws IOException {

        PREFIX = propUtils.getProperty("convert.input.stream.into.file.temp.path");
        SUFFIX = propUtils.getProperty("convert.input.stream.into.file.temp.file.type");

        final File tempFile = File.createTempFile(PREFIX, SUFFIX);
        tempFile.deleteOnExit();
        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            IOUtils.copy(in, out);
        }
        return tempFile;
    }
}
