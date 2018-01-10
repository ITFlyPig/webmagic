package com.wangyuelin.model;

/**
 * 做菜的步骤
 */
public class StepBean {
    private String name;//步骤的名称
    private String img;//步骤的图片
    private String index;//步骤的索引
    private String tip;//提示

    public String getName() {
        return name;
    }

    public String getImg() {
        return img;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    @Override
    public String toString() {
        String re = "index:" + getIndex() +"name:" + getName() + " img:" + getImg() + " tip:" + getTip();
        return re;
    }
}
