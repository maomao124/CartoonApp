package mao.cartoonapp.entity;

/**
 * Project name(项目名称)：解析漫画网站
 * Package(包名): mao.entity
 * Class(类名): CartoonItemRequestBody
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2022/10/11
 * Time(创建时间)： 20:40
 * Version(版本): 1.0
 * Description(描述)： 无
 */
public class CartoonItemRequestBody
{
    /**
     * 请求的漫画的id
     */
    private int id;

    /**
     * 第二个id，一般为1
     */
    private int id2;

    /**
     * Instantiates a new Cartoon item request body.
     *
     * @param id  the id
     * @param id2 the id 2
     */
    public CartoonItemRequestBody(int id, int id2)
    {
        this.id = id;
        this.id2 = id2;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public int getId()
    {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     * @return the id
     */
    public CartoonItemRequestBody setId(int id)
    {
        this.id = id;
        return this;
    }

    /**
     * Gets id 2.
     *
     * @return the id 2
     */
    public int getId2()
    {
        return id2;
    }

    /**
     * Sets id 2.
     *
     * @param id2 the id 2
     * @return the id 2
     */
    public CartoonItemRequestBody setId2(int id2)
    {
        this.id2 = id2;
        return this;
    }

    @Override
    @SuppressWarnings("all")
    public String toString()
    {
        final StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append("id：").append(id).append('\n');
        stringbuilder.append("id2：").append(id2).append('\n');
        return stringbuilder.toString();
    }
}
