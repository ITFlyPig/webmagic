package com.wangyuelin.model;

import org.apache.http.util.TextUtils;

import java.awt.*;

public class Tag {
    private String tag;//标签
    private String tagText;//标签的文字
    private String childSText;//子标签的文字

    public String getTag() {
        return tag;
    }

    public String getTagText() {
        return tagText;
    }

    public String getChildSText() {
        return childSText;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setTagText(String tagText) {
        this.tagText = tagText;
    }

    public void setChildSText(String childSText) {
        this.childSText = childSText;
    }

    @Override
    public String toString() {
        return " tagText:" + tagText + "  childSText:" + childSText;
    }
}
