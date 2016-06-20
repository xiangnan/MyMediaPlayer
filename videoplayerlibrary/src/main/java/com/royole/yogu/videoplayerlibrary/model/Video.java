package com.royole.yogu.videoplayerlibrary.model;

import android.media.Image;

/**
 * Video Model
 * Author  yogu
 * Since  2016/6/20
 */


public class Video {
    /** vedio id */
    private String vID;
    /** vedio web url */
    private String vURL;
    /** vedio img shot */
    private String imageShotPath;
    /** vedio name */
    private String vTitle;
    /** vedio desc */
    private String desc;

    public Video(String vID, String vURL, String imageShotPath, String vTitle, String desc) {
        this.vID = vID;
        this.vURL = vURL;
        this.imageShotPath = imageShotPath;
        this.vTitle = vTitle;
        this.desc = desc;
    }

    public String getvID() {
        return vID;
    }

    public void setvID(String vID) {
        this.vID = vID;
    }

    public String getvURL() {
        return vURL;
    }

    public void setvURL(String vURL) {
        this.vURL = vURL;
    }

    public String getImageShotPath() {
        return imageShotPath;
    }

    public void setImageShotPath(String imageShotPath) {
        this.imageShotPath = imageShotPath;
    }

    public String getvTitle() {
        return vTitle;
    }

    public void setvTitle(String vTitle) {
        this.vTitle = vTitle;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "Video{" +
                "vID='" + vID + '\'' +
                ", vURL='" + vURL + '\'' +
                ", imageShotPath=" + imageShotPath +
                ", vTitle='" + vTitle + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}
