package com.wangyuelin.processor;

import org.apache.http.util.TextUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.xsoup.Xsoup;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 获取水果的功效
 */
public class FruitFuncProcessor implements PageProcessor {
    private Site site = Site.me().setRetryTimes(3).setSleepTime(0)
    .setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");;
    private static final String url = "https://www.baidu.com/s?wd=芒果的功效与作用";//抽取的网址

    private String[] fruits = new String[]{"香蕉","芒果", "苹果", "葡萄"};//要查询的水果放这里

    private boolean isAdded;//是否添加了查询的url
    private HashMap<String, String> urlMap = new HashMap<String, String>();

    public void process(Page page) {

        //添加所有水果的功效查询url
        if (!isAdded) {
            isAdded = true;
            int size = fruits.length;
            for (int i = 0; i < size; i++) {
                String fruit = fruits[i];
                String url = getQueryUrl(fruit);
                if (!TextUtils.isEmpty(url)) {
                    page.addTargetRequest(url);
                    urlMap.put(url, fruit);
                }
            }
        }

        String url = page.getUrl().get();
        if(TextUtils.isEmpty(url)){
           return;
        }
        String fruit = urlMap.get(url);
        if (url.startsWith(FUNC_PREFIX)){//功效
            String func = page.getHtml().xpath("//div[@class='op_exactqa_s_answer']/text()").get();
            String icon = page.getHtml().xpath("//div[@class='op_exactqa_main']//img/@src").get();
            String detail = getDetailUrl(fruit);
            page.addTargetRequest(detail);


        }else if (url.startsWith(DETAIL_PREFIX)){//详情
            String desc = page.getHtml().xpath("//div[@class='lemma-summary']/div").get();
            String watedDesc = parseDeatil(desc);
            System.out.println( "简介：" + watedDesc);

        }

    }

    public Site getSite() {
        return site;
    }


    private String getQueryStr(String fruit) {
        if (TextUtils.isEmpty(fruit)) {
            return null;
        }
        return fruit + "的功效与作用";

    }


    private static String FUNC_PREFIX = "https://www.baidu.com/s?wd=";
    private static String DETAIL_PREFIX = "https://baike.baidu.com/item/";
    /**
     * 获取查询的url
     *
     * @param fruit
     * @return
     */
    private String getQueryUrl(String fruit) {

        String queryStr = getQueryStr(fruit);
        if (TextUtils.isEmpty(fruit)) {
            return null;
        }
        return FUNC_PREFIX + queryStr;

    }

    private String getDetailUrl(String fruit){
        return DETAIL_PREFIX + fruit;

    }

    public static void main(String[] args) {
        Spider.create(new FruitFuncProcessor()).addPipeline(new ConsolePipeline()).addUrl(url).thread(4).run();
    }



    private String parseDeatil(String deatilHtml){
        String noTagsStr = removeAllTags(deatilHtml);
        noTagsStr = noTagsStr.replaceAll(" ", "");//移除空格
        noTagsStr = noTagsStr.replaceAll("\n", "");//移除换行
        return noTagsStr;
    }

    /**
     * 移除所有的html标签
     * @param str
     */
    private String removeAllTags(String str){
        if (TextUtils.isEmpty(str)){
            return null;
        }
        String pattern = "<.+?>";
        return str.replaceAll(pattern, "");
    }

}
