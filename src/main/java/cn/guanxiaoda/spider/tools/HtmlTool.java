package cn.guanxiaoda.spider.tools;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;
import org.jsoup.nodes.Element;

import java.util.List;
import java.util.Objects;

/**
 * @author guanxiaoda
 * @date 2018/5/9
 */
public class HtmlTool {

    private String html;
    private Document doc;

    public HtmlTool(String html) {
        this.html = html;
        init();
    }

    public static void main(String[] args) throws XPatherException {

//        List<Element> students = tool.findElementsByXpath("//student");
//        List<Element> elements = tool.findElementsByValue("Harry Potter");

//        System.out.println(students);


        HtmlCleaner cleaner = new HtmlCleaner();
        TagNode node = cleaner.clean("http://blog.guanxiaoda.cn/");
        Object[] result = node.evaluateXPath("//a[contains(.,'Farming')]");
        for (Object obj : result) {
            System.out.println(obj);
        }

    }

    private void init() {
        Objects.requireNonNull(html);
        try {
            doc = DocumentHelper.parseText(html);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    public List<Element> findElementsByXpath(String xpath) {
        List elements = doc.selectNodes(xpath);
        return elements;
    }

    public List<Element> findElementsByValue(String value) {
        if (Strings.isNullOrEmpty(value)) {
            return Lists.newArrayList();
        }
        return null;
    }
}
