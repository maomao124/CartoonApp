package mao.cartoonapp.service;

import com.alibaba.fastjson.JSON;
import mao.cartoonapp.constant.URLConstant;
import mao.cartoonapp.entity.Cartoon;
import mao.cartoonapp.entity.CartoonItem;
import mao.cartoonapp.entity.CartoonItemRequestBody;
import mao.cartoonapp.net.HTTP;
import mao.cartoonapp.net.RestfulHTTP;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Project name(项目名称)：解析漫画网站
 * Package(包名): mao.service
 * Class(类名): CartoonServiceImpl
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2022/10/11
 * Time(创建时间)： 19:41
 * Version(版本): 1.0
 * Description(描述)： 无
 */

public class CartoonServiceImpl implements CartoonService
{

    private final RestfulHTTP http;

    public CartoonServiceImpl(RestfulHTTP http)
    {
        this.http = http;
    }


    /**
     * 得到漫画列表json
     *
     * @param urlString url字符串
     * @return {@link List}<{@link Cartoon}>
     */
    public List<Cartoon> getCartoonListByJson(String urlString, Map<String, String> requestHeader)
    {
        String json = http.GET(urlString, requestHeader);
        //System.out.println(json);
        List<Cartoon> cartoonList = JSON.parseArray(json, Cartoon.class);
        return cartoonList;
    }

    public List<Cartoon> getCartoonListByJson(Map<String, String> requestHeader, int pageNumber, int type)
    {
        return this.getCartoonListByJson("http://m.qiman57.com/ajaxf/?page_num=" + pageNumber + "&type=" + type,
                requestHeader);
    }

    @Override
    public List<Cartoon> getCartoonList(String urlString, int type)
    {
        Map<String, String> map = new HashMap<>();
        map.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/106.0.0.0 Safari/537.36 Edg/106.0.1370.37");
        map.put("Referer", urlString);
        map.put("Host", URLConstant.host);
        List<Cartoon> list = new ArrayList<>(100);
        for (int i = 0; i < 6; i++)
        {
            List<Cartoon> cartoonList = getCartoonListByJson(map, i, type);
            list.addAll(cartoonList);
        }
        return list;
    }

    @Override
    public List<CartoonItem> getCartoonItem(int id)
    {
        List<CartoonItem> cartoonItemByHtml = getCartoonItemByHtml(id);
        List<CartoonItem> cartoonItemByJson = getCartoonItemByJson(id);
        cartoonItemByHtml.addAll(cartoonItemByJson);
        return cartoonItemByHtml;
    }

    public List<CartoonItem> getCartoonItemByHtml(int id)
    {
        List<CartoonItem> list = new ArrayList<>(20);
        Map<String, String> map = new HashMap<>();
        map.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/106.0.0.0 Safari/537.36 Edg/106.0.1370.37");
        map.put("Referer", URLConstant.bookChapterUrl);
        map.put("Host", URLConstant.host);
        String html = http.GET(URLConstant.baseUrl + id + "/", map);
        //System.out.println(html);

        Document document = Jsoup.parse(html);
        //System.out.println(document);
        Element element = document.getElementsByClass("catalog-list").first();
        //System.out.println(element);
        if (element == null)
        {
            return list;
        }
        Elements li = element.getElementsByTag("li");
        //System.out.println(li);
        if (li.size() == 0)
        {
            return list;
        }
        for (Element liItem : li)
        {
            //System.out.println(liItem + "\n");
            String itemId = liItem.attr("data-id");
            //System.out.println(itemId);
            Element a = liItem.getElementsByTag("a").first();
            String name;
            if (a == null)
            {
                name = "";
            }
            else
            {
                try
                {
                    name = a.html();
                }
                catch (Exception e)
                {
                    name = "";
                }
            }
            //System.out.println(name);

            CartoonItem cartoonItem = new CartoonItem(itemId, name);
            list.add(cartoonItem);
        }
        return list;
    }

    public List<CartoonItem> getCartoonItemByJson(int id)
    {
        Map<String, String> map = new HashMap<>();
        map.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/106.0.0.0 Safari/537.36 Edg/106.0.1370.37");
        map.put("Referer", URLConstant.bookChapterUrl);
        map.put("Host", URLConstant.host);
        String body = "id=" + id + "&id2=1";
        String s = http.request(URLConstant.bookChapterUrl, "POST", map, body);
        return JSON.parseArray(s, CartoonItem.class);
    }


    /**
     * 搜索
     *
     * @param keyword 关键字
     * @return {@link List}<{@link Cartoon}>
     */
    @Override
    public List<Cartoon> search(String keyword)
    {
        List<Cartoon> list = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        map.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/106.0.0.0 Safari/537.36 Edg/106.0.1370.37");
        map.put("Host", URLConstant.host);
        String url = URLConstant.searchUrl + keyword;
        String html = http.GET(url, map);
        if (html == null)
        {
            return list;
        }
        Document document = Jsoup.parse(html);
        //System.out.println(document);
        Element element = document.getElementsByClass("search-result").first();
        //System.out.println(element);
        if (element == null)
        {
            return list;
        }
        Elements elements = element.getElementsByClass("comic-list-item clearfix");
        //System.out.println(elements.size() + "\n\n");
        //System.out.println(elements);
        for (Element element1 : elements)
        {
            String id = element1.attr("data-cid");
            //System.out.println(id);
            Element img = element1.getElementsByTag("img").first();
            //System.out.println(img);
            String imgUrl;
            imgUrl = img != null ? img.attr("src") : null;
            String name = Objects.requireNonNull(Objects.requireNonNull(element1.getElementsByClass("comic-name").
                    first()).getElementsByTag("a").first()).html();
            String author = Objects.requireNonNull(element1.getElementsByClass("comic-author").first()).html();
            String remarks = Objects.requireNonNull(element1.getElementsByClass("comic-update-at").first()).html();
            //System.out.println(imgUrl);
            //System.out.println(name);
            //System.out.println(author);
            //System.out.println(remarks);
            Cartoon cartoon = new Cartoon()
                    .setId(id)
                    .setName(name)
                    .setAuthor(author)
                    .setRemarks(remarks)
                    .setImgUrl(imgUrl);
            list.add(cartoon);
        }
        return list;
    }
}
