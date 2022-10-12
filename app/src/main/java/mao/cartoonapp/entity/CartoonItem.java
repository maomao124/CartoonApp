package mao.cartoonapp.entity;

/**
 * Project name(项目名称)：解析漫画网站
 * Package(包名): mao.entity
 * Class(类名): CartoonItem
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2022/10/11
 * Time(创建时间)： 20:36
 * Version(版本): 1.0
 * Description(描述)： 无
 */


public class CartoonItem
{
    /**
     * id
     */
    private String id;

    /**
     * 章节名字
     */
    private String name;

    /**
     * Instantiates a new Cartoon item.
     */
    public CartoonItem()
    {
    }

    /**
     * Instantiates a new Cartoon item.
     *
     * @param id   the id
     * @param name the name
     */
    public CartoonItem(String id, String name)
    {
        this.id = id;
        this.name = name;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public String getId()
    {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     * @return the id
     */
    public CartoonItem setId(String id)
    {
        this.id = id;
        return this;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     * @return the name
     */
    public CartoonItem setName(String name)
    {
        this.name = name;
        return this;
    }

    @Override
    @SuppressWarnings("all")
    public String toString()
    {
        final StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append("id：").append(id).append('\n');
        stringbuilder.append("name：").append(name).append('\n');
        return stringbuilder.toString();
    }
}
