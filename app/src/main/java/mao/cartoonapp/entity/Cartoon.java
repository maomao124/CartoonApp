package mao.cartoonapp.entity;

import android.graphics.Bitmap;

/**
 * Project name(项目名称)：解析漫画网站
 * Package(包名): mao.entity
 * Class(类名): Cartoon
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2022/10/11
 * Time(创建时间)： 19:42
 * Version(版本): 1.0
 * Description(描述)： 无
 */


public class Cartoon
{
    /**
     * id
     */
    private String id;

    /**
     * 名字
     */
    private String name;

    /**
     * 作者
     */
    private String author;

    /**
     * 最后一章节
     */
    private String remarks;

    /**
     * img url
     */
    private String imgUrl;


    /**
     * 位图
     */
    private Bitmap bitmap;

    /**
     * Instantiates a new Cartoon.
     */
    public Cartoon()
    {

    }

    /**
     * Instantiates a new Cartoon.
     *
     * @param id      the id
     * @param name    the name
     * @param author  the author
     * @param remarks the remarks
     * @param imgUrl  the img url
     */
    public Cartoon(String id, String name, String author, String remarks, String imgUrl)
    {
        this.id = id;
        this.name = name;
        this.author = author;
        this.remarks = remarks;
        this.imgUrl = imgUrl;
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
    public Cartoon setId(String id)
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
    public Cartoon setName(String name)
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
    public Cartoon setAuthor(String author)
    {
        this.author = author;
        return this;
    }

    /**
     * Gets remarks.
     *
     * @return the remarks
     */
    public String getRemarks()
    {
        return remarks;
    }

    /**
     * Sets remarks.
     *
     * @param remarks the remarks
     * @return the remarks
     */
    public Cartoon setRemarks(String remarks)
    {
        this.remarks = remarks;
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
    public Cartoon setImgUrl(String imgUrl)
    {
        this.imgUrl = imgUrl;
        return this;
    }

    public Bitmap getBitmap()
    {
        return bitmap;
    }

    public Cartoon setBitmap(Bitmap bitmap)
    {
        this.bitmap = bitmap;
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

        Cartoon cartoon = (Cartoon) o;

        if (getId() != null ? !getId().equals(cartoon.getId()) : cartoon.getId() != null)
        {
            return false;
        }
        if (getName() != null ? !getName().equals(cartoon.getName()) : cartoon.getName() != null)
        {
            return false;
        }
        if (getAuthor() != null ? !getAuthor().equals(cartoon.getAuthor()) : cartoon.getAuthor() != null)
        {
            return false;
        }
        if (getRemarks() != null ? !getRemarks().equals(cartoon.getRemarks()) : cartoon.getRemarks() != null)
        {
            return false;
        }
        return getImgUrl() != null ? getImgUrl().equals(cartoon.getImgUrl()) : cartoon.getImgUrl() == null;
    }

    @Override
    public int hashCode()
    {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getAuthor() != null ? getAuthor().hashCode() : 0);
        result = 31 * result + (getRemarks() != null ? getRemarks().hashCode() : 0);
        result = 31 * result + (getImgUrl() != null ? getImgUrl().hashCode() : 0);
        return result;
    }

    @Override
    @SuppressWarnings("all")
    public String toString()
    {
        final StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append("id：").append(id).append('\n');
        stringbuilder.append("name：").append(name).append('\n');
        stringbuilder.append("author：").append(author).append('\n');
        stringbuilder.append("remarks：").append(remarks).append('\n');
        stringbuilder.append("imgUrl：").append(imgUrl).append('\n');
        return stringbuilder.toString();
    }
}
