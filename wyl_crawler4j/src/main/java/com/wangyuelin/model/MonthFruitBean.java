package com.wangyuelin.model;

import java.util.List;

public class MonthFruitBean {
    private String month;
    private List<String> fruits;
    private String fruitStr;

    public String getMonth() {
        return month;
    }

    public List<String> getFruits() {
        return fruits;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public void setFruits(List<String> fruits) {
        this.fruits = fruits;
    }

    public String getFruitStr() {
        return fruitStr;
    }

    public void setFruitStr(String fruitStr) {
        this.fruitStr = fruitStr;
    }
}
