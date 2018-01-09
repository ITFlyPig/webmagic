package com.wangyuelin.model;

/**
 * 水果的做法
 */
public class CookBookBean {
    private String img;//图片
    private String name;//菜谱的名称
    private String intro;//菜谱的简介
    private String detailUrl;//

    public String getImg() {
        return img;
    }

    public String getName() {
        return name;
    }

    public String getIntro() {
        return intro;
    }

    public String getDetailUrl() {
        return detailUrl;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public void setDetailUrl(String detailUrl) {
        this.detailUrl = detailUrl;
    }

    @Override
    public String toString() {
        String out = "img:" + getImg() +  " name:" + getName() + " intro:" + getIntro() + " detail:" + getDetailUrl();
        return out;
    }
}
