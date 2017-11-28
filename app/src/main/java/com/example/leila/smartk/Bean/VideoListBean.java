package com.example.leila.smartk.Bean;

/**
 *  主页面视频的Bean
 * Created by Leila on 2017/9/14.
 */

public class VideoListBean {
    Integer img;//list图片
    String tvTitle;//摄像头信息
    public VideoListBean(Integer img, String title){
        this.img = img;
        this.tvTitle = title;
    }
    public Integer getImg() {
        return img;
    }

    public void setImg(Integer img) {
        this.img = img;
    }

    public String getTvTitle() {
        return tvTitle;
    }

    public void setTvTitle(String tvTitle) {
        this.tvTitle = tvTitle;
    }




}
