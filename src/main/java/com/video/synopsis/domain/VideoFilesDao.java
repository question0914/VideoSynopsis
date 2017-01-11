package com.video.synopsis.domain;


public class VideoFilesDao {

    private String fileName;
    private String modifyDate;
    private String downloadLink;

    public VideoFilesDao() {
    }

    public VideoFilesDao(String fileName, String modifyDate, String downloadLink) {
        this.fileName = fileName;
        this.modifyDate = modifyDate;
        this.downloadLink = downloadLink;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(String modifyDate) {
        this.modifyDate = modifyDate;
    }

    public String getDownloadLink() {
        return downloadLink;
    }

    public void setDownloadLink(String downloadLink) {
        this.downloadLink = downloadLink;
    }
}
