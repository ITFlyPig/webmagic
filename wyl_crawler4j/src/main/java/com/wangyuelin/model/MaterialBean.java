package com.wangyuelin.model;

/**
 * 用料
 */
public class MaterialBean {
    private String name;//用料的名称
    private String num;//用料的数量
    private String img;//材料的图片

    public String getName() {
        return name;
    }

    public String getNum() {
        return num;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNum(String num) {
        this.num = num;
    }

    @Override
    public String toString() {
        String re = "name:" + getName() + " num:" + getNum() + " img:" + getImg();
        return re;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
