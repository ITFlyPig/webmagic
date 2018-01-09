package com.wangyuelin.model;

import java.util.List;

public class CookBookDetailBean {
    private String name;//菜名
    private List<MaterialBean> main;//主料
    private List<MaterialBean> assistant;//辅料
    private List<StepBean> steps;//做菜的步骤

    public String getName() {
        return name;
    }

    public List<MaterialBean> getMain() {
        return main;
    }

    public List<MaterialBean> getAssistant() {
        return assistant;
    }

    public List<StepBean> getSteps() {
        return steps;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMain(List<MaterialBean> main) {
        this.main = main;
    }

    public void setAssistant(List<MaterialBean> assistant) {
        this.assistant = assistant;
    }

    public void setSteps(List<StepBean> steps) {
        this.steps = steps;
    }
}
