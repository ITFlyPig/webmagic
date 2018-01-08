package com.wangyuelin.processor;

import com.wangyuelin.model.MonthFruitBean;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.xsoup.XElements;
import us.codecraft.xsoup.Xsoup;

import java.util.ArrayList;
import java.util.List;

public class MonthFruitProcessor implements PageProcessor {
    private Site site = Site.me().setRetryTimes(3).setSleepTime(0);
    private static final String url = "https://jingyan.baidu.com/article/9113f81b042ffa2b3214c7bc.html";//抽取的网址

    public void process(Page page) {
        List<String> p = page.getHtml().css(".content-listblock-text > p").all();
        int size = p.size();

        ArrayList<MonthFruitBean> list = new ArrayList<MonthFruitBean>();
        for (int i = 0; i < size; i++){
            XElements monthElements = Xsoup.select(p.get(i), "//strong/text()");
            String month =  monthElements.get();
            XElements fruitElements = Xsoup.select(p.get(i), "//p/text()");
            String fruitStr = fruitElements.get();


            MonthFruitBean bean = new MonthFruitBean();
            bean.setMonth(month);
            bean.setFruitStr(fruitStr);
            list.add(bean);

//            System.out.println("月份：" + month.replaceAll(" ", "") + " 水果：" + fruitStr.replaceAll(" ", ""));
        }

        page.putField("monthFruit", list);



    }

    public Site getSite() {
        return site;
    }


    public static void main(String[] args){
        Spider.create(new MonthFruitProcessor()).addPipeline(new ConsolePipeline()).addUrl(url).thread(4).run();
    }

}
