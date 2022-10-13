package mao.cartoonapp.service;



import java.util.List;

import mao.cartoonapp.entity.Cartoon;
import mao.cartoonapp.entity.CartoonItem;

/**
 * Project name(项目名称)：解析漫画网站
 * Package(包名): mao.service
 * Interface(接口名): CartoonService
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2022/10/11
 * Time(创建时间)： 19:41
 * Version(版本): 1.0
 * Description(描述)： 无
 */

public interface CartoonService
{
    List<Cartoon> getCartoonList(String urlString, int type);

    List<CartoonItem> getCartoonItem(int id);

    /**
     * 搜索
     *
     * @param keyword 关键字
     * @return {@link List}<{@link Cartoon}>
     */
    List<Cartoon> search(String keyword);
}
