package com.wangyuelin.processor;

import com.wangyuelin.model.CookBookBean;
import com.wangyuelin.model.CookBookDetailBean;
import com.wangyuelin.model.MaterialBean;
import com.wangyuelin.model.StepBean;
import org.apache.http.util.TextUtils;
import org.apache.log4j.BasicConfigurator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * 处理每种水果做法的类
 */
public class CookbookProcessor implements PageProcessor {
    private Site site = Site.me().setRetryTimes(3).setSleepTime(0)
            .setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");

    private static String PREFIX = "http://so.meishi.cc/?&q=";
    private static String SUFIX = "&sort=onclick";
    private static String COOK_PREFIX = "http://www.meishij.net/zuofa";

    public void process(Page page) {
        String url = page.getUrl().get();
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (url.startsWith(COOK_PREFIX)) {//解析得到具体的做法
            String main = page.getHtml().xpath("//div[@class='yl zl clearfix']").get();//住料
            parseMain(getElement(main));
            String assistant = page.getHtml().xpath("//div[@class='yl fuliao clearfix']").get();//辅料
            parseAssistant(getElement(assistant));

            String stepStr = page.getHtml().xpath("//div[@class='editnew edit]").get();//步骤
            parseSteps(stepStr);


        } else if (url.startsWith(PREFIX)) {//解析得到菜谱的简介
            List<String> cookBookList = page.getHtml().xpath("//div[@class='search2015_cpitem']").all();
           List<CookBookBean> cookList = parseIntro(cookBookList);
            for (CookBookBean cookBookBean:cookList){
                page.addTargetRequest(cookBookBean.getDetailUrl());
                break;
            }
        }


    }

    public Site getSite() {
        return site;
    }

    private static String getUrl(String fruit) {
        if (TextUtils.isEmpty(fruit)) {
            return null;
        }
        return PREFIX + fruit + SUFIX;

    }

    public static void main(String[] args) {
        BasicConfigurator.configure();

//        Spider.create(new CookbookProcessor()).addPipeline(new ConsolePipeline()).addUrl("http://so.meishi.cc/?&q=%E8%8A%92%E6%9E%9C&sort=time").thread(4).run();
    }

    private List<CookBookBean> parseIntro(List<String> cookBookList) {
        if (cookBookList == null) {
            return null;
        }
        ArrayList<CookBookBean> books = new ArrayList<CookBookBean>();
        int size = cookBookList.size();
        for (int i = 0; i < size; i++) {
            CookBookBean cookBookBean = new CookBookBean();
            Element element = getElement(cookBookList.get(i));
            Element img = element.getElementsByTag("img").first();
            cookBookBean.setImg(img.attr("src"));
            Element a = element.getElementsByClass("cpn").first();
            cookBookBean.setName(a.text());
            cookBookBean.setDetailUrl(a.attr("href"));
            Element span = element.getElementsByTag("span").first();
            cookBookBean.setIntro(span.text());

            books.add(cookBookBean);
        }
        return books;
    }

    /**
     * 获取节点
     *
     * @param div
     * @return
     */
    private Element getElement(String div) {
        return Jsoup.parse(div).getAllElements().first().getElementsByTag("div").first();
    }

    private List<CookBookDetailBean> parseDetail(String detal) {

        return null;
    }

    /**
     * 解析住料
     *
     * @param div
     * @return
     */
    private List<MaterialBean> parseMain(Element div) {
        ArrayList<MaterialBean> list = new ArrayList<MaterialBean>();

        Elements lis = div.getElementsByTag("li");
        int size = lis.size();
        for (int i = 0; i < size; i++) {
            MaterialBean materialBean = new MaterialBean();
            Element li = lis.get(i);
            Element cDiv = li.getElementsByTag("div").first();
            Element a = cDiv.getElementsByTag("a").first();
            if (a != null){
                materialBean.setName(a.text());//材料的名称
            }
            Element span = cDiv.getElementsByTag("span").first();
            if (span != null){
                materialBean.setNum(span.text());//材料的数量
            }
            Element img = li.getElementsByTag("img").first();
            if (img != null){
                materialBean.setImg(img.attr("src"));
            }


            list.add(materialBean);

        }


        return list;
    }

    /**
     * 解析副料
     *
     * @param div
     * @return
     */
    private List<MaterialBean> parseAssistant(Element div) {
        ArrayList<MaterialBean> list = new ArrayList<MaterialBean>();

        Elements lis = div.getElementsByTag("li");
        int size = lis.size();
        for (int i = 0; i < size; i++) {
            MaterialBean materialBean = new MaterialBean();
            Element li = lis.get(i);
            Element h4 = li.getElementsByTag("h4").first();
            if (h4 != null){
                materialBean.setName(h4.getElementsByTag("a").first().text());//名称
            }
            Element span = li.getElementsByTag("span").first();
            if (span != null){
                materialBean.setNum(span.text());//数量
            }
            Element img = li.getElementsByTag("img").first();
            if (img != null){
                materialBean.setImg(img.attr("src"));
            }


            list.add(materialBean);

        }

        return list;
    }


    /**
     * 解析得到步骤
     *
     * @param stepStr
     * @return
     */
    private List<StepBean> parseSteps( String stepStr) {
        if (stepStr == null) {
            return null;
        }
        Element stepContent = getElement(stepStr);
        Elements childs = stepContent.children();
        int size = childs.size();
        ArrayList<StepBean> stepList = new ArrayList<StepBean>();
        for (int i = 0; i < size; i++){
            Element child = childs.get(i);
           String tagName = child.tagName();
            StepBean oneStep = null;
           if (tagName.equalsIgnoreCase("div")){
               oneStep =  parsePerStep(child);
           }else if (tagName.equalsIgnoreCase("p")){//纯图片
               Element img = child.children().first();
               if (img.tagName().equalsIgnoreCase("img")){
                   oneStep = new StepBean();
                   oneStep.setImg(img.attr("src"));
               }

           }else if (tagName.equalsIgnoreCase("h2")){
               oneStep = new StepBean();
               oneStep.setName(child.text());//标题
               //获取标题的子内容，其实是平级的
               int next = i + 1;
               if (next < size){
                   String tip  = childs.get(next).text();
                   oneStep.setTip(tip);
               }
           }
           if (oneStep != null){
               stepList.add(oneStep);
           }
        }
        return stepList;

    }

    /**
     * 解析一个步骤的div
     *
     * @param element
     * @return
     */
    private StepBean parsePerStep(Element element) {
        if (element == null) {
            return null;
        }
        StepBean step = new StepBean();
        Element em = element.getElementsByTag("em").first();
        if (em != null) {
            step.setIndex(em.text());
        }

        Element p = element.getElementsByTag("p").first();
        if (p != null) {
            step.setName(p.text());
        }

        Element img = element.getElementsByTag("img").first();
        if (img != null) {
            step.setImg(img.attr("src"));
        }

        return step;
    }

}
