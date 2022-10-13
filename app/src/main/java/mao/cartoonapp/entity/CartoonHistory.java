package mao.cartoonapp.entity;


/**
 * Project name(项目名称)：CartoonApp
 * Package(包名): mao.cartoonapp.entity
 * Class(类名): CartoonHistory
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2022/10/13
 * Time(创建时间)： 13:55
 * Version(版本): 1.0
 * Description(描述)： 历史记录，取不到章节名称
 */


public class CartoonHistory
{
    /**
     * 漫画的id
     */
    private String id1;
    /**
     * 章节的id
     */
    private String id2;
    /**
     * 漫画名字
     */
    private String name;
    /**
     * 漫画作者
     */
    private String author;

    /**
     * 最后一次更新时间
     */
    private Long lastTime;

    /**
     * img url
     */
    private String imgUrl;

    /**
     * Instantiates a new Cartoon history.
     */
    public CartoonHistory()
    {

    }

    /**
     * Instantiates a new Cartoon history.
     *
     * @param id1      the id 1
     * @param id2      the id 2
     * @param name     the name
     * @param author   the author
     * @param lastTime the last time
     * @param imgUrl   the img url
     */
    public CartoonHistory(String id1, String id2, String name, String author, Long lastTime, String imgUrl)
    {
        this.id1 = id1;
        this.id2 = id2;
        this.name = name;
        this.author = author;
        this.lastTime = lastTime;
        this.imgUrl = imgUrl;
    }

    /**
     * Gets id 1.
     *
     * @return the id 1
     */
    public String getId1()
    {
        return id1;
    }

    /**
     * Sets id 1.
     *
     * @param id1 the id 1
     * @return the id 1
     */
    public CartoonHistory setId1(String id1)
    {
        this.id1 = id1;
        return this;
    }

    /**
     * Gets id 2.
     *
     * @return the id 2
     */
    public String getId2()
    {
        return id2;
    }

    /**
     * Sets id 2.
     *
     * @param id2 the id 2
     * @return the id 2
     */
    public CartoonHistory setId2(String id2)
    {
        this.id2 = id2;
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
    public CartoonHistory setName(String name)
    {
        this.name = name;
        return this;
    }

    /**
     * Gets author.
     *
     * @return the author
     */
    public String getAuthor()
    {
        return author;
    }

    /**
     * Sets author.
     *
     * @param author the author
     * @return the author
     */
    public CartoonHistory setAuthor(String author)
    {
        this.author = author;
        return this;
    }

    /**
     * Gets last time.
     *
     * @return the last time
     */
    public Long getLastTime()
    {
        return lastTime;
    }

    /**
     * Sets last time.
     *
     * @param lastTime the last time
     * @return the last time
     */
    public CartoonHistory setLastTime(Long lastTime)
    {
        this.lastTime = lastTime;
        return this;
    }

    /**
     * Gets img url.
     *
     * @return the img url
     */
    public String getImgUrl()
    {
        return imgUrl;
    }

    /**
     * Sets img url.
     *
     * @param imgUrl the img url
     * @return the img url
     */
    public CartoonHistory setImgUrl(String imgUrl)
    {
        this.imgUrl = imgUrl;
        return this;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        CartoonHistory that = (CartoonHistory) o;

        if (getId1() != null ? !getId1().equals(that.getId1()) : that.getId1() != null)
        {
            return false;
        }
        if (getId2() != null ? !getId2().equals(that.getId2()) : that.getId2() != null)
        {
            return false;
        }
        if (getName() != null ? !getName().equals(that.getName()) : that.getName() != null)
        {
            return false;
        }
        if (getAuthor() != null ? !getAuthor().equals(that.getAuthor()) : that.getAuthor() != null)
        {
            return false;
        }
        if (getLastTime() != null ? !getLastTime().equals(that.getLastTime()) : that.getLastTime() != null)
        {
            return false;
        }
        return getImgUrl() != null ? getImgUrl().equals(that.getImgUrl()) : that.getImgUrl() == null;
    }

    @Override
    public int hashCode()
    {
        int result = getId1() != null ? getId1().hashCode() : 0;
        result = 31 * result + (getId2() != null ? getId2().hashCode() : 0);
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getAuthor() != null ? getAuthor().hashCode() : 0);
        result = 31 * result + (getLastTime() != null ? getLastTime().hashCode() : 0);
        result = 31 * result + (getImgUrl() != null ? getImgUrl().hashCode() : 0);
        return result;
    }

    @Override
    @SuppressWarnings("all")
    public String toString()
    {
        final StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append("id1：").append(id1).append('\n');
        stringbuilder.append("id2：").append(id2).append('\n');
        stringbuilder.append("name：").append(name).append('\n');
        stringbuilder.append("author：").append(author).append('\n');
        stringbuilder.append("lastTime：").append(lastTime).append('\n');
        stringbuilder.append("imgUrl：").append(imgUrl).append('\n');
        return stringbuilder.toString();
    }
}

