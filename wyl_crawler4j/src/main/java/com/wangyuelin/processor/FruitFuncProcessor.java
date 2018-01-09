package com.wangyuelin.processor;

import com.wangyuelin.model.Tag;
import org.apache.http.util.TextUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 获取水果的功效
 */
public class FruitFuncProcessor implements PageProcessor {
    private Site site = Site.me().setRetryTimes(3).setSleepTime(0)
            .setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");

    private static final String url = "https://www.baidu.com/s?wd=芒果的功效与作用";//抽取的网址

    private String[] fruits = new String[]{"葡萄"};//要查询的水果放这里

    private boolean isAdded;//是否添加了查询的url
    private HashMap<String, String> urlMap = new HashMap<String, String>();

    private static String[] NEED_TITLE = new String[]{"不宜", "文化", "历史", "食用"};//需要的标题

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
        if (TextUtils.isEmpty(url)) {
            return;
        }
        String fruit = urlMap.get(url);
        if (url.startsWith(FUNC_PREFIX)) {//功效
            String func = page.getHtml().xpath("//div[@class='op_exactqa_s_answer']/text()").get();
            String icon = page.getHtml().xpath("//div[@class='op_exactqa_main']//img/@src").get();
            String detail = getDetailUrl(fruit);
            page.addTargetRequest(detail);


        } else if (url.startsWith(DETAIL_PREFIX)) {//详情
            String desc = page.getHtml().xpath("//div[@class='lemma-summary']/div").get();//简介
            String watedDesc = parseDeatil(desc);
            System.out.println("简介：" + watedDesc);
            List<String> divs = page.getHtml().xpath("//div[@label-module]").all();
            System.out.println("内容页的数据：" + divs.size());
            List<Tag> list = pasreNeedtag(divs);
//            System.out.println("各个事项获取的结果：" + list);
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

    private String getDetailUrl(String fruit) {
        return DETAIL_PREFIX + fruit;

    }

    public static void main(String[] args) {
        Spider.create(new FruitFuncProcessor()).addPipeline(new ConsolePipeline()).addUrl(url).thread(4).run();
    }


    private String parseDeatil(String deatilHtml) {
        String noTagsStr = removeAllTags(deatilHtml);
        noTagsStr = noTagsStr.replaceAll(" ", "");//移除空格
        noTagsStr = noTagsStr.replaceAll("\n", "");//移除换行
        return noTagsStr;
    }


    /**
     * 解析得到文化相关的标签
     *
     * @param
     * @return
     */
    private List<Tag> pasreNeedtag(List<String> divs) {

        if (divs == null) {
            return null;
        }

        int size = divs.size();
        ArrayList<Tag> tagList = new ArrayList<Tag>();
        for (int i = 0; i < size; i++) {
            Document dc = Jsoup.parse(divs.get(i));
            Elements elements = dc.getAllElements();
            if (elements.size() == 0) {
                continue;
            }

            Element child = elements.first().getElementsByTag("div").first();

            if (isTitleTag(child) && isNeedTag(child)) {//开始获取标题下的内容
                System.out.println("开始解析标签：---------------\n" + divs.get(i) + "\n -------------------------------");
                Tag tag = new Tag();
                String tagStr = child.text();
                tag.setTagText(tagStr);//标题
                System.out.println("标签标题：" + tagStr);

                int temp = i + 1;
                Element next = null;
                if (temp < size) {
                    next = Jsoup.parse(divs.get(temp)).getAllElements().first().getElementsByTag("div").first();

                }
                if (next == null) {
                    continue;
                }
                while (isItem(next)) {//是标题对应下的item，应该获取
                    String itemStr = next.text();
                    if (!TextUtils.isEmpty(itemStr)) {
                        String childStr = tag.getChildSText();
                        if (childStr == null) {
                            childStr = "";
                        }
                        tag.setChildSText(childStr + "\n" + itemStr);
                    }


                    //在接着取下一个
                    temp++;
                    if (temp < size) {
                        next = Jsoup.parse(divs.get(temp)).getAllElements().first().getElementsByTag("div").first();
                    } else {
                        temp--;//避免数组越界
                        break;
                    }
                }
                System.out.println(" 标题下的内容：" + tag.getChildSText());
                i = --temp;//和for循环中的++做抵消
                tagList.add(tag);

            }

        }

        return tagList;
    }


    /**
     * 移除所有的html标签
     *
     * @param str
     */
    private String removeAllTags(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        String pattern = "<.+?>";
        return str.replaceAll(pattern, "");
    }

    /**
     * 检测是否是标题标签
     *
     * @param element
     * @return
     */
    private boolean isTitleTag(Element element) {
        if (element == null) {
            return false;
        }
        String lableStr = element.attr("label-module");
        System.out.println("label-module属性对应的值：" + lableStr);
        if (!TextUtils.isEmpty(lableStr) && lableStr.equalsIgnoreCase("para-title")) {
            System.out.println("是否是标题" + "true");
            return true;
        }
        System.out.println("是否是标题" + "false");
        return false;
    }

    /**
     * 判断是否应该继续
     *
     * @param element
     * @return
     */
    private boolean isItem(Element element) {
        String tagStr = element.text();
        if (TextUtils.isEmpty(tagStr)) {
            return false;
        }
        String lableStr = element.attr("label-module");
        if (TextUtils.isEmpty(lableStr)) {
            return false;
        }
        if (lableStr.equalsIgnoreCase("para")) {
            return true;
        }
        return false;
    }

    /**
     * 是否是需要的标签
     *
     * @param element
     * @return
     */
    private boolean isNeedTag(Element element) {
        if (element == null) {
            return false;
        }
        String text = getElementTitle(element);
        System.out.println("element的标题:" + text);
        if (TextUtils.isEmpty(text)) {
            return false;
        }

        int size = NEED_TITLE.length;
        for (int i = 0; i < size; i++) {
            if (text.contains(NEED_TITLE[i])) {

                return true;
            }
        }


        return false;


    }

    /**
     * 递归获取当期那元素和子元素的文本内容
     *
     * @return
     */
    private String getElementHText(Element element) {
        String res = "";
        int childs = element.children().size();
        if (childs == 0) {
            if ("h1 h2 h3 h4 h5 h6".contains(element.tagName())) {
                res += element.text();
            }

        } else if (childs > 0) {
            if ("h1 h2 h3 h4 h5 h6".contains(element.tagName())) {
                res += element.text();
            }
            for (Element child : element.children()) {
                res += getElementHText(child);
            }

        }
        return res;
    }

    /**
     * 获取元素的标题
     *
     * @param element
     * @return
     */
    private String getElementTitle(Element element) {

        return getElementHText(element);

    }


   private void test(){

       String html = "<div class=\"para-title level-2\" label-module=\"para-title\"> \n" +
               " <h2 class=\"title-text\"><span class=\"title-prefix\">苹果</span>历史</h2> \n" +
               " <a class=\"edit-icon j-edit-link\" data-edit-dl=\"10\" href=\"javascript:;\"><em class=\"cmn-icon wiki-lemma-icons wiki-lemma-icons_edit-lemma\"></em>编辑</a> \n" +
               "</div>";

       Element element = Jsoup.parse(html).getAllElements().first().getElementsByTag("div").first();
       String res = getElementHText(element);
       System.out.println("res:" + res);
   }

}
